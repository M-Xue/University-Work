from src.data_store import data_store
from src.error import AccessError, InputError
from src.helper import find_user, find_user_in_channel, store_history

def channels_list_v1(auth_user_id):
    '''
    Provides a list of all channels and their associated details that the authorised user is part of.

    Arguments:
        auth_user_id (int)    - id of an authorised user

    Exceptions:
        AccessError - Occurs when auth_user_id does not refer to a valid user

    Return Value:
        Returns {channels} on the condition that all exceptions are passed
    '''
    
    # grabbing data
    data = data_store.get()
    channels_list = data['channels']
    result_channels = []
    for channel in channels_list:
        # check if user is part of that channel
        if find_user_in_channel(auth_user_id, channel):
        # append to list
            result_channels.append({"channel_id": channel['channel_id'], "name": channel['name']})
    return {"channels" : result_channels}

def channels_listall_v1(auth_user_id):
    '''
    Provides a list of all channels, including private channels, and their associated details that the authorised user is part of.

    Arguments:
        auth_user_id (int)    - ID of an authorised user

    Exceptions:
        AccessError - Occurs when auth_user_id is not valid

    Return Value:
        Returns {channels} on the condition that all exceptions are passed
    '''

    list_channels = []
    store = data_store.get()
    channels = store['channels']

    # Loop through channels and add ID and names to a dictionary
    for channel in channels:
        list_channels.append({"channel_id": channel['channel_id'], "name": channel['name']})
    return {'channels': list_channels}

def channels_create_v1(auth_user_id, name, is_public):
    '''
    Creates a new channel with the given name that is either a public or private channel.
    The user who created it automatically joins the channel.

    Arguments:
        auth_user_id (int)    - id of an authorised user
        name         (str)    - name of a channel that is to be created
        is_public    (bool)   - determines wether the channel will be public(True) or private(False)

    Exceptions:
        InputError  - Occurs when length of name is less than 1 or more than 20 characters.
        AccessError - Occurs when auth_user_id is not valid

    Return Value:
        Returns {channel_id} on the condition that a new channel is successfully created
    '''

    if len(name) > 20:
        raise InputError(description='Length of name is more than 20 characters')
    if len(name) < 1:
        raise InputError(description='Length of name is less than 1 character')

    data = data_store.get()
    channels_list = data['channels']

    # create a new channel
    channel_id = len(channels_list) + 1
    # get user details to add to owner_members and all_members
    user = data['users'][auth_user_id]
    user_details = {
        'u_id': auth_user_id,
        'email': user['email'],
        'name_first': user['name_first'],
        'name_last': user['name_last'],
        'handle_str': user['handle_str']
    }

    new_channel = {
        'channel_id': channel_id,
        'name': name,
        'auth_user_id': auth_user_id,
        'is_public': is_public,
        'owner_members': [user_details],
        'all_members': [user_details],
        'standup_active' : False,
        'standup_time_finish' : None,
        'standup_start_user' : None,
        'standup_messages' : [],
    }

    channels_list.append(new_channel)

    data['channels'] = channels_list
    data_store.set(data)
    store_history(auth_user_id, channels_create_v1)
    # append to list
    return {
        'channel_id': channel_id
    }
