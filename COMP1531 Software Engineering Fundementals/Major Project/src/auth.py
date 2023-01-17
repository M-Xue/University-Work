from src.data_store import data_store
from src.error import InputError, AccessError
import re
from src.helper import store_history
from src.security_helper import find_session_id, decode_jwt, hash, authenticate_token

from src.config import url
BASE_URL = url
from src.data_store import save_datastore

import smtplib
import random
from src.helper import find_user


def auth_login_v1(email, password):
    '''
    Given a registered user's email and password, returns their user ID value. Validates the given data by checking if the given email is associated with an existing account and if the given password matches that account if found.

    Arguments:
        email    (string)    - User's email associated with their account
        password (string)    - User's password associated with their account

    Exceptions:
        InputError  - Occurs when:
                        - the given email has no associated registered account
                        - the given password does not match the password associated to the account for the given email

    Return Value:
        Returns a dictionary that contains the user ID associated with the logged into account on condition that no expections are raised
    '''

    users_list = data_store.get()['users']

    # Existing Email Validation
    email_list = []
    for user in users_list:
            email_list.append(user['email'])
    if email not in email_list:
        raise InputError(description='This email has not been registered.')

    # Getting User Data
    requested_user = {}
    for user in users_list:
        if user['email'] == email:
            requested_user = user
    
    # Password Validation
    if requested_user['password'] != hash(password):
        raise InputError(description='Incorrect Password.')

    return {
        'auth_user_id': requested_user['u_id'],
    }



def auth_register_v1(email, password, name_first, name_last):
    '''
    Given a user's first and last name, email address, and password, create a new account for them and return a new user ID. Also automatically generates a handle for the user. Adds this user to the data store as a dictionary containing email, password, first name, last name, generated handle, generated user ID and a boolean signifying if they are a global owner of Seams (you are automatically a global owner if you are the first user to join Seams).

    Arguments:
        email      (string)    - New user's email they want associated with their account
        password   (string)    - New user's password they want associated with their account
        name_first (string)    - New user's first name
        name_last  (string)    - New user's last name

    Exceptions:
        InputError  - Occurs when:
                        - first name character length is less than 1
                        - first name character length is more than 50
                        - last name character length is less than 1
                        - last name character length is more than 50
                        - password is less than 6 characters
                        - email format is not valid
                        - email is already registered

    Return Value:
        Returns a dictionary that contains the user ID associated with the newly registered account on condition that no expections are raised
    '''

    # First Name Validation
    if len(name_first) < 1:
        raise InputError(description='First name character length is less than 1. It must be larger or equal to 1')
    elif len(name_first) > 50:
        raise InputError('First name character length is more than 50. It must be smaller or equal to 50')
    
    # Last Name Validation
    if len(name_last) < 1:
        raise InputError(description='Last name character length is less than 1. It must be larger or equal to 1')
    elif len(name_last) > 50:
        raise InputError('Last name character length is more than 50. It must be smaller or equal to 50')
    
    # Password Validation
    if len(password) < 6:
        raise InputError(description='Password is less than 6 characters. It must be at least 6 characters.')
    

    # Regex Email Validation
    regexp = re.compile(r'^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$')
    if not regexp.search(email):
        raise InputError(description='Email format is not valid.')

    data = data_store.get()
    users_list = data['users']

    # Unique Email Validation
    email_list = []
    for user in users_list:
            email_list.append(user['email'])
    if email in email_list:
        raise InputError(description='This email is already registered.')

    # User Handles
    handle_list = []
    for user in users_list:
        handle_list.append(user['handle_str'])

    lowercase_name = (name_first + name_last).lower()
    new_user_handle = ''.join(ch for ch in lowercase_name if ch.isalnum())

    shortened_new_user_handle = (new_user_handle[:20]) if len(new_user_handle) > 20 else new_user_handle
    
    handleDuplicateCounter = 0
    if shortened_new_user_handle in handle_list:
        while True:
            if (shortened_new_user_handle + str(handleDuplicateCounter)) in handle_list:
                handleDuplicateCounter += 1
            else:
                shortened_new_user_handle = shortened_new_user_handle + str(handleDuplicateCounter)
                break


    # Creating user IDs assuming users cannot be deleted. Will need to change this if they can be deleted.
    user_id = len(users_list)
    new_user = {}
    if len(users_list) == 0:
        new_user = {
            'u_id': user_id,
            'email': email,
            'name_first': name_first,
            'name_last': name_last,
            'handle_str': shortened_new_user_handle,
            'password': hash(password),
            'permission_id': 1, # The first user in the entire Seams application is automatically a global owner
            'notifications': [],
            'profile_img_url': f'{BASE_URL}images/member-default.jpg',
        }
    else: 
        new_user = {
            'u_id': user_id,
            'email': email,
            'name_first': name_first,
            'name_last': name_last,
            'handle_str': shortened_new_user_handle,
            'password': hash(password),
            'permission_id': 2,
            'notifications' : [],
            'profile_img_url': f'{BASE_URL}images/member-default.jpg',
        }

    users_list.append(new_user)

    data['users'] = users_list
    data_store.set(data)
    store_history(user_id, auth_register_v1)
    return {
        'auth_user_id': user_id,
    }




def logout(token):
    '''
    Given a token, removes the session id of that token from the data store, thereby logging out.

    Arguments:
        token    (string)    - Token for the session that's about to be logged out
        
    Exceptions:
        AccessError  - Occurs when:
                        - the given token is invalid

    Return Value:
        Returns nothing
    '''

    token_data = decode_jwt(token)
    
    authenticate_token(token_data)

    data = data_store.get()
    sessions_list = data['sessions']
     
    removed_session = token_data['session_id']

    for session in sessions_list:
        if session['session_id'] == removed_session:
            sessions_list.remove(session)
    
    data['sessions'] = sessions_list
    data_store.set(data)

    return {}


# Password Reset Email Request
def send_recovery_code(email):
    '''
    Checks if the given email is a registered user. If so, it sends an email with a code to the given email. This code is then stored in the data store along with the associated account to the email. If the email is not registered, no errors are raised. Deletes all currently active codes associated with given email

    Arguments:
        email (string)    - Email of account requesting for password reset
        ...

    Exceptions:
        None

    Return Value:
        Returns empty object
    '''

    data = data_store.get()
    users = data["users"]
    registered_emails = [user['email'] for user in users]

    # Checking if the given email was valid
    if email in registered_emails:

        # Getting the user of interest
        new_pwd_user = {}
        for user in users:
            if user['email'] == email:
                new_pwd_user = user        
        user_u_id = new_pwd_user['u_id']

        # Logging out of previous sessions
        sessions = data_store.get()['sessions']
        new_sessions = []
        for session in sessions:
            if session['user_id'] != user_u_id:
                new_sessions.append(session)
        data['sessions'] = new_sessions

        data_store.set(data)

        new_code = None
        found_unique_code = False
        while not found_unique_code:
            secret_code = random.randint(100000,999999) 
            active_codes = data['new_pwd_codes']
            active = False
            for user_code in active_codes:
                if user_code['code'] == secret_code:
                    active = True
                    break
            if active == False:
                found_unique_code = True
                new_code = secret_code
        
        ### Invalidating all previous codes
        new_valid_codes = []
        for user_code in data['new_pwd_codes']:
            if user_code['u_id'] != user_u_id:
                new_valid_codes.append(user_code)
        data['new_pwd_codes'] = new_valid_codes

        new_user_code = {
            'u_id': user_u_id,
            'code': new_code,
        }
        data['new_pwd_codes'].append(new_user_code)
        data_store.set(data)

        # Code taken from here: https://www.courier.com/blog/three-ways-to-send-emails-using-python-with-code-tutorials/
        gmail_user = 'comp1531testemail1@gmail.com'
        gmail_password = 'bgark12345'

        sent_from = gmail_user
        to = [f'{email}']
        subject = 'COMP1531 Seams Password Reset Code'
        body = f'COMP1531 Seams Password Reset Code for {email}\nNew code is: {new_code}'

        email_text = """\
        From: %s
        To: %s
        Subject: %s

        %s
        """ % (sent_from, ", ".join(to), subject, body)

        smtp_server = smtplib.SMTP_SSL('smtp.gmail.com', 465)
        smtp_server.ehlo()
        smtp_server.login(gmail_user, gmail_password)
        smtp_server.sendmail(sent_from, to, email_text)
        smtp_server.close()

    save_datastore()
    return {}

def reset_password_code_input(reset_code, new_password):
    '''
    Takes a reset code sent to a given email from send_recovery_code() as well as a new password. If the reset code is valid, it will reset the password of the associated email user. 

    Arguments:
        reset_code (string)    - Reset code eamiled to valid registered password
        new_password (string)    - New password of user associated with email
        ...

    Exceptions:
        InputError  - Occurs when ...
                        Reset code is not a valid reset code

    Return Value:
        Returns empty object
    '''

    # New Password Length Validation
    if len(new_password) < 6:
        raise InputError(description='Password is less than 6 characters. It must be at least 6 characters.')

    reset_code_int = int(reset_code)

    data = data_store.get()

    reset_pwd_codes = data['new_pwd_codes']

    # Checking if the reset code is valid
    valid_code = False
    user_code_pair = {}

    for user_code in reset_pwd_codes:
        if user_code['code'] == reset_code_int:
            valid_code = True
            user_code_pair = user_code
            break

    if valid_code == False:
        raise InputError(description='Reset code is not a valid reset code')
    
    # Changing password
    user_id = user_code_pair['u_id']

    user = find_user(user_id)

    data['users'].remove(user)
    user['password'] = hash(new_password)
    data['users'].append(user)


    ### Invalidating all codes
    new_valid_codes = []
    for user_code in reset_pwd_codes:
        if user_code['u_id'] != user_id:
            new_valid_codes.append(user_code)

    data['new_pwd_codes'] = new_valid_codes
    data_store.set(data)
    save_datastore()
    return {}

