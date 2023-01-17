import hashlib
import jwt
from src.error import AccessError
from src.data_store import data_store
from src.helper import find_user


SESSION_TRACKER = 0
SECRET = 'COMP1531'

def generate_new_session_id(user_id):
    """Generates a new sequential session ID

    Returns:
        number: The next session ID
    """
    global SESSION_TRACKER
    SESSION_TRACKER += 1
    data = data_store.get()
    sessions_list = data['sessions']
    sessions_list.append({'session_id': SESSION_TRACKER, 'user_id': user_id})
    data['sessions'] = sessions_list
    data_store.set(data)

    return SESSION_TRACKER

def hash(input_string):
    """Hashes the input string with sha256

    Args:
        input_string ([string]): The input string to hash

    Returns:
        string: The hexidigest of the encoded string
    """
    return hashlib.sha256(input_string.encode()).hexdigest()


def generate_jwt(user_id):
    """Generates a JWT using the global SECRET

    Args:
        user_id ([string]): The user_id
        session_id ([string], optional): The session id, if none is provided will
                                         generate a new one. Defaults to None.

    Returns:
        string: A JWT encoded string
    """

    session_id = generate_new_session_id(user_id)
    return jwt.encode({'auth_user_id': user_id, 'session_id': session_id}, SECRET, algorithm='HS256')

def decode_jwt(encoded_jwt):
    """Decodes a JWT string into an object of the data

    Args:
        encoded_jwt ([string]): The encoded JWT as a string

    Returns:
        Object: An object storing the body of the JWT encoded string
    """
    return jwt.decode(encoded_jwt, SECRET, algorithms=['HS256'])

def find_session_id(session_id):
    '''
    Checks if session ID is in data store

    Arguments:
        session_id (int)    - Searched for session ID

    Return Value:
        Returns session ID if found in data store
        Returns None if session ID is not found
    '''
    store = data_store.get()
    session_list = store['sessions']

    for session in session_list:
        if session['session_id'] == session_id:
            return session_id
    return None

def authenticate_token(token_data):
    '''
    Checks if the token data has been altered.

    Arguments:
        token_data (string)    - The encoded JWT as a string
        ...

    Exceptions:
        InputError  - Occurs when ...
                        Invalid user ID in token
                        Invalid session in token

    Return Value:
        Returns nothing
    '''
    user = find_user(token_data['auth_user_id'])
    if user is None:
        raise AccessError(description='Invalid user ID in token')
    
    session = find_session_id(token_data['session_id'])
    if session is None:
        raise AccessError(description='Invalid session in token')
    
def check_valid_token(token):
    """Checks whether a JWT token is valid

    Args:

        token(string) : token that is being checked


    Returns:
        AccessError if token is invalid
        decoded token if token is valid
    """
    try:
        decode_jwt(token)
    except Exception as error:
        raise AccessError(description='Invalid token') from error

    authenticate_token(decode_jwt(token))
