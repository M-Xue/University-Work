from src.data_store import data_store
from src.error import AccessError, InputError
from src.security_helper import decode_jwt, check_valid_token
from src.helper import find_user_in_channel, find_user_in_dms

def search_v1(token, query_str):
    '''
    Allows an authorised user to search for a collection of messages containing a given query string
    in the channels/DMs they are currently a member of

    Arguments:
        token       (string)    - token of an authorised user of Seams
        query_str   (string)    - string that the user is searching for

    Exceptions:
        InputError  - Occurs when length of query_str is less than 1 or over 1000 characters

        AccessError - Occurs when token is invalid

    Return Value:
        Returns {messages} on the condition that the user was able to successfully search for messages
    '''
    # Check if token is invalid
    check_valid_token(token)
    # get auth_user_id from token
    auth_user_id = decode_jwt(token)['auth_user_id']

    # Check if query_str length is valid
    if len(query_str) < 1 or len(query_str) > 1000:
        raise InputError(description='Invalid query_str length')
    
    # Gets all messages the user can view
    data = data_store.get()
    user_messages = []
    # looping through channels
    for channel in data['channels']:
        if find_user_in_channel(auth_user_id, channel) is not None:
            for message in data['messages']:
                if message['channel_id'] == channel['channel_id']:
                    user_messages.append(message)
    # looping through dms
    for dm in data['dms']:
        if find_user_in_dms(auth_user_id, dm) is True:
            for message in data['dm_messages']:
                if message['dm_id'] == dm['dm_id']:
                    user_messages.append(message)
    
    # Searching for all messages which contain query_str
    search_messages = []
    for message in user_messages:
        if query_str.lower() in message['message'].lower():
            copy = message.copy()
            for react in copy['reacts']:
                if auth_user_id in react['u_ids']:
                    react.update({'is_this_user_reacted': True})
                else:
                    react.update({'is_this_user_reacted': False})
            
            search_messages.append(
                {
                    'message_id': message['message_id'],
                    'u_id': message['auth_user_id'],
                    'message': message['message'],
                    'time_sent': message['time_sent'],
                    'reacts': copy['reacts'],
                    'is_pinned': message['is_pinned'],
                }
            )

    return {'messages': search_messages}
