from src.data_store import data_store
from src.error import AccessError

def check_valid_channel(channel_id):
    '''
    checks if the channel is valid

    Arguments:
        channel_id (int)    - id of channel

    Return Value:
        returns if channel is valid
    '''
    store = data_store.get()
    channel_is_valid = False
    for chanl in store['channels']:
        if chanl['channel_id'] == channel_id:
            channel_is_valid = True
            break

    return channel_is_valid

def msg_id_valid(msg_id):
    '''
    checks if msg_id is valid

    Arguments:
        msg_id (int)    - ID of msg

    Return Value:
        returns is_valid_msg
    '''
    store = data_store.get()
    is_valid_msg = False
    for msg in store['messages']:
        if msg_id == msg['message_id']:
            is_valid_msg = True 
            break

    for dm_msg in store['dm_messages']:
        if msg_id == dm_msg['message_id']:
            is_valid_msg = True
            break
    return is_valid_msg

def return_message_is_valid(message_id):
    '''
    check if message is valid

    Arguments:
        message_id (int)    - ID of dm of interest

    Return Value:
        valid_msg
    '''
    store = data_store.get()
    valid_msg = {}
    for msg in store['messages']:
        if message_id == msg['message_id']:
            valid_msg = msg 
            break 

    for dm_msg in store['dm_messages']:
        if message_id == dm_msg['message_id']:
            valid_msg = dm_msg 
            break 

    return valid_msg

def check_user_has_channel_permission(channel_id, auth):
    '''
    Gives you the dictionary with the data of the dm of given dm id

    Arguments:
        channel_id (int)    - ID of channel
        auth (int) - auth

    Return Value:
        returns true if user has permission
    '''
    store = data_store.get()
    channel_dict = {}
    for ch in store['channels']:
        if ch['channel_id'] == channel_id:
            channel_dict = ch 
            break 
    for auths in channel_dict['owner_members']:
        if auths == auth:
            return True 
    return False

def check_user_has_dm_permission(dm_id, auth_user_id):
    '''
    Gives you the dictionary with the data of the dm of given dm id

    Arguments:
        dm_id (int)    - ID of dm
        auth_user_id (int) - auth_user_id

    Return Value:
        returns true if user has permission
    '''
    store = data_store.get()
    dm_dict = {}
    for dm in store['dms']:
        if dm['dm_id'] == dm_id:
            dm_dict = dm 
            break 
    if dm_dict['owners_id'] == auth_user_id:
        return True

    return False
