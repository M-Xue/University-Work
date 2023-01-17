from pydoc import describe
from src.data_store import data_store
from src.error import InputError, AccessError
import re

from src.security_helper import decode_jwt, authenticate_token
from src.helper import find_user
from src.data_store import save_datastore

import os
from PIL import Image
from src.config import url
import urllib.request
BASE_URL = url
import imghdr


def user_profile(token, u_id):
    '''
    Checks if token is valid and then gets the requested user from data store

    Arguments:
        token (string)    - Encoded JWT token with user ID and session ID
        u_id (string)    - Requested user ID
        ...

    Exceptions:
        InputError  - Occurs when ...
                        Invalid User ID
        AccessError - Occurs when ...
                        Invalid token

    Return Value:
        Returns object with user details 
    '''
    if u_id == '':
        raise InputError(description='Invalid User ID')

    token_data = decode_jwt(token)
    authenticate_token(token_data)
    user = find_user(int(u_id))
    if user is None:
        raise InputError(description='Invalid User ID')

    return { 'user': user }


def users_all(token):
    '''
    Returns list of all user details

    Arguments:
        token (string)    - Encoded JWT token with user ID and session ID
        ...

    Exceptions:
        InputError  - Occurs when ...
                        Invalid User ID
                        First name character length is less than 1
                        First name character length is more than 50
                        Last name character length is less than 1
                        Last name character length is more than 50
        AccessError - Occurs when ...
                        Invalid token

    Return Value:
        Returns object with a list of user objects
    '''

    token_data = decode_jwt(token)
    authenticate_token(token_data)
    store = data_store.get()
    users = store['users']
    user_list = []

    # Do not call removed users
    for user in users:
        if not (user['name_first'] == 'Removed' and user['name_last'] == 'user'):
            user_list.append(user)

    return { 'users': user_list }


def set_name(token_data, name_first, name_last):
    '''
    Changes given users handle with given handle

    Arguments:
        token_data (string)    - Encoded JWT token with user ID and session ID
        handle (string)    - New handle
        ...

    Exceptions:
        InputError  - Occurs when ...
                        First name character length is less than 1. It must be larger or equal to 1
                        First name character length is more than 50. It must be smaller or equal to 50
                        Last name character length is less than 1. It must be larger or equal to 1
                        Last name character length is more than 50. It must be smaller or equal to 50
        AccessError - Occurs when ...
                        Invalid token

    Return Value:
        Returns empty object
    '''
    
    token_data = decode_jwt(token_data)
    authenticate_token(token_data)

    # First Name Validation
    if len(name_first) < 1:
        raise InputError(description='First name character length is less than 1. It must be larger or equal to 1')
    elif len(name_first) > 50:
        raise InputError(description='First name character length is more than 50. It must be smaller or equal to 50')
    
    # Last Name Validation
    if len(name_last) < 1:
        raise InputError(description='Last name character length is less than 1. It must be larger or equal to 1')
    elif len(name_last) > 50:
        raise InputError(description='Last name character length is more than 50. It must be smaller or equal to 50')

    data = data_store.get()
    users_list = data['users']

    user = find_user(int(token_data['auth_user_id']))
    users_list.remove(user)
    user['name_first'] = name_first
    user['name_last'] = name_last
    users_list.append(user)

    return {}


def set_email(token_data, email):
    '''
    Changes given users email with given email

    Arguments:
        token_data (string)    - Encoded JWT token with user ID and session ID
        email (string)    - New email
        ...

    Exceptions:
        InputError  - Occurs when ...
                        Email format is not valid.
                        This email is already registered
        AccessError - Occurs when ...
                        Invalid token

    Return Value:
        Returns empty object
    '''
    
    token_data = decode_jwt(token_data)
    authenticate_token(token_data)

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

    user = find_user(int(token_data['auth_user_id']))
    users_list.remove(user)
    user['email'] = email
    users_list.append(user)

    data['users'] = users_list
    data_store.set(data)

    return {}


def set_handle(token_data, handle):
    '''
    Changes given users handle with given handle

    Arguments:
        token_data (string)    - Encoded JWT token with user ID and session ID
        handle (string)    - New handle
        ...

    Exceptions:
        InputError  - Occurs when ...
                        Handle length less than 3
                        Handle length more than 20
                        Handle already taken by another user
        AccessError - Occurs when ...
                        Invalid token

    Return Value:
        Returns empty object
    '''
    
    token_data = decode_jwt(token_data)
    authenticate_token(token_data)

    # Handle Length Validation
    if len(handle) < 3:
        raise InputError(description='Handle length less than 3')
    elif len(handle) > 20:
        raise InputError(description='Handle length more than 20')

    # Alphanumeric Handle Validation
    if not handle.isalnum():
        raise InputError(description='Handle contains characters that are not alphanumeric') from InputError

    data = data_store.get()
    users_list = data['users']
    for user in users_list:
        if user['handle_str'] == handle:
            raise InputError(description='Handle already taken by another user')

    user = find_user(int(token_data['auth_user_id']))

    # Changing the name of the user's profile image because it's based on their handle string
    if user['profile_img_url'] != f'{BASE_URL}images/member-default.jpg':
        old_user_handle = user['handle_str']
        os.rename(f'src/images/profiles/{old_user_handle}profileimg.jpg', f'src/images/profiles/{handle}profileimg.jpg')
        user['profile_img_url'] = f'{BASE_URL}images/profiles/{handle}profileimg.jpg'

    users_list.remove(user)
    user['handle_str'] = handle
    users_list.append(user)

    data['users'] = users_list
    data_store.set(data)

    return {}

def save_profile_img(token_data, img_url, x_start, y_start, x_end, y_end):
    '''
    Saves an image to associate with a given user. The image is stored in a folder on the server. The url in the user dict for images is also altered to be the url for the profile of that user.

    Arguments:
        token_data (string)    - Encoded JWT token with user ID and session ID
        img_url (string)    - The url to download the profile picture from
        x_start (string)    - The starting pixel of the horizontal axis for cropping
        y_start (string)    - The starting pixel of the vertical axis for cropping
        x_end (string)    - The ending pixel of the horizontal axis for cropping
        y_end (string)    - The ending pixel of the vertical axis for cropping
        ...

    Exceptions:
        InputError  - Occurs when ...
                        x_end is less than or equal to x_start or y_end is less than or equal to y_start
                        img_url returned an HTTP status other than 200, or some other errors occured when attempting to retrieve the image
                        Image uploaded is not a JPG
                        x_start or x_end are not within the dimensions of the image at the URL
                        y_start or y_end are not within the dimensions of the image at the URL
        AccessError - Occurs when ...
                        Invalid token

    Return Value:
        Returns empty object
    '''

    try:
        token_data = decode_jwt(token_data)
    except:
        raise AccessError(description='Invalid token') from AccessError
    authenticate_token(token_data)
    
    x_start = int(x_start)
    x_end = int(x_end)

    y_start = int(y_start)
    y_end = int(y_end)

    if x_start >= x_end or y_start >= y_end:
        raise InputError(description='x_end is less than or equal to x_start or y_end is less than or equal to y_start')
    
    try: 
        urllib.request.urlretrieve(img_url, "src/images/tmp-img.jpg")
    except:
        raise InputError(description="img_url returned an HTTP status other than 200, or some other errors occured when attempting to retrieve the image") from InputError
    

    if imghdr.what('src/images/tmp-img.jpg') != 'jpeg':
        os.remove('src/images/tmp-img.jpg')
        raise InputError(description='Image uploaded is not a JPG')


    imageObject = Image.open("src/images/tmp-img.jpg")
    image_width = imageObject.size[0]
    if x_start < 0 or x_end > image_width:
        os.remove('src/images/tmp-img.jpg')
        raise InputError(description='x_start or x_end are not within the dimensions of the image at the URL')
    image_height = imageObject.size[1]
    if y_start < 0 or y_end > image_height:
        os.remove('src/images/tmp-img.jpg')
        raise InputError(description='y_start or y_end are not within the dimensions of the image at the URL')


    cropped = imageObject.crop((x_start, y_start, x_end, y_end))


    user = find_user(token_data['auth_user_id'])
    user_handle = user['handle_str']
    saved_image_url = f'{user_handle}profileimg.jpg'
    if user['profile_img_url'] != f'{BASE_URL}images/member-default.jpg':
        os.remove(f'src/images/profiles/{saved_image_url}')
    
    data = data_store.get()
    data['users'].remove(user)


    cropped.save(f'src/images/profiles/{saved_image_url}')
    user['profile_img_url'] = f'{BASE_URL}images/profiles/{saved_image_url}'
    
    os.remove('src/images/tmp-img.jpg')

    
    data['users'].append(user)
    data_store.set(data)

    save_datastore()
    return {}




# https://gitlab.cse.unsw.edu.au/COMP1531/22T1/content/-/tree/master/lecture-code/7.4

# https://www.youtube.com/watch?v=4bDp36tBDBc



# http://img.freepik.com/free-vector/cute-astronaut-super-flying-cartoon-illustration_138676-3259.jpg?w=2000
# http://cdn.mos.cms.futurecdn.net/iC7HBvohbJqExqvbKcV3pP.jpg
# https://upload.wikimedia.org/wikipedia/commons/4/41/Sunflower_from_Silesia2.jpg
# https://i.pinimg.com/550x/16/77/42/167742f17fc9b58a6e1084618d244196.jpg

# PNG
# https://static.wikia.nocookie.net/onepiece/images/6/6d/Monkey_D._Luffy_Anime_Post_Timeskip_Infobox.png/revision/latest?cb=20200429191518
