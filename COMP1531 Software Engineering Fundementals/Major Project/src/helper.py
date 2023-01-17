from src.data_store import data_store
from src.error import InputError, AccessError
import time

def find_channel(channel_id):
    '''
    Gives you the dictionary with the data of the channel of given chennel id

    Arguments:
        channel_id (int)    - ID of channel of interest

    Return Value:
        Returns dictionary of channel on condition channel exists in data_store
        Returns None on condition that channel does not exist
    '''
    store = data_store.get()
    channels = store['channels']
    for channel in channels:
        if channel['channel_id'] == channel_id:
            return channel
    return None

def find_user_in_channel(user_id, channel):
    '''
    Checks if a given user is in a given channel

    Arguments:
        user_id (int)           - ID of user of interest
        channel (dictionary)    - dictionary of channel of interest

    Return Value:
        Returns dictionary of user on condition user exists in channel
        Returns None on condition that user is not in the channel
    '''
    members = channel['all_members']
    for user in members:
        if user['u_id'] == user_id:
            return user
    return None

def find_user_in_channel_id(user_id, channel_id):
    '''
    Checks if a given user is in a given channel

    Arguments:
        channel_id (int)    - ID of channel of interest
        user_id    (int)    - ID of user of interest

    Return Value:
        Returns dictionary of user on condition user exists in channel
        Returns None on condition that user is not in the channel
    '''
    channel = find_channel(channel_id)
    if channel == None:
        return None
    members = channel['all_members']
    for user in members:
        if user['u_id'] == user_id:
            return user
    return None

def find_user(u_id):
    '''
    Checks if user exists in data_store and returns the user data in a dictionary

    Arguments:
        u_id (int)    - ID of user of interest

    Return Value:
        Returns dictionary of user on condition user exists in data_store
        Returns None on condition that user does not exist
    '''
    store = data_store.get()
    users = store['users']
    for user in users:
        if user['u_id'] == u_id:
            return user
    return None

def is_channel_public(channel_id):
    '''
    Checks if channel is public

    Arguments:
        channel_id (int)    - ID of channel of interest

    Return Value:
        Returns True if channel is public
        Returns False if channel is private
    '''
    channel = find_channel(channel_id)
    if channel['is_public'] == True:
        return True
    return False

def is_global_owner(user_id):
    '''
    Checks if user is global owner

    Arguments:
        user_id (int)    - ID of user of interest

    Return Value:
        Returns True if user is global owner
        Returns False if user is not global owner
    '''
    user = find_user(user_id)
    if user['permission_id'] == 1:
        return True
    return False

def find_message(message_id):
    '''
    Finds message from specified ID

    Arguments:
        message_id (int)    - ID of messaged

    Return Value:
        Returns Message if found
    '''
    store = data_store.get()
    messages = store['messages']
    for message in messages:
        if message_id == message['message_id']:
            return message
        
    dm_messages = store['dm_messages']
    for dm_message in dm_messages:
        if message_id == dm_message['message_id']:
            return dm_message
    
    raise InputError(description="Message ID doesn't exist")

def find_message_in_channel(message_id):
    '''
    Finds message in a channel

    Arguments:
        message_id (int)    - ID of messaged

    Return Value:
        Returns Message if found
    '''
    store = data_store.get()
    messages = store['messages']
    for message in messages:
        if message_id == message['message_id']:
            return message
    return None

def find_message_in_dm(message_id):
    '''
    Finds message in a dm

    Arguments:
        message_id (int)    - ID of messaged

    Return Value:
        Returns Message if found
    '''
    store = data_store.get()
    dm_messages = store['dm_messages']
    for dm_message in dm_messages:
        if message_id == dm_message['message_id']:
            return dm_message
    return None

def find_dm(dm_id):
    '''
    Finds the dm of specified dm_id

    Arguments:
        dm_id (int)    - dm_id of specified dm

    Return Value:
        Returns the dictionary of the DM
    '''
    store = data_store.get()
    dms = store['dms']
    for dm in dms:
        if dm_id == dm['dm_id']:
            return dm
    return None

def is_user_channel_owner(user, channel):
    '''
    Checks if they are in owners_members of channel

    Arguments:
        user (dict)    - Dict of user of interest
        channel (dict) - Dict of channel of interest

    Return Value:
        Returns True if user is in channel owner
        Returns False if user is not in channel owner
    '''
    channel_owners = channel['owner_members']
    for owner in channel_owners:
        if user['u_id'] == owner['u_id']:
            return True
    return False

def check_if_owner(user_id, channel_id):
    '''
    Checks if a given user is an owner of given channel

    Arguments:
        user_id    (int)    - ID of user of interest
        channel_id (int)    - ID of channel of interest

    Return Value:
        Returns dictionary of user on condition user is an owner in the channel
        Returns None on condition that user is not an owner in the channel
    '''
    channel = find_channel(channel_id)
    if channel is None:
        return None
    owners = channel['owner_members']
    for user in owners:
        if user['u_id'] == user_id:
            return user
    return None

def is_u_ids_valid(users_list):
    '''
    Checks if a list of u_ids are valid

    Arguments:
        user_id (list)

    Return Value:
        Returns True if all the users in the list are valid
        Returns False if any of the users in the list are invalid
    '''
    for user in users_list:
        if find_user(user) is None:
            return False
    return True

def find_user_in_dms(auth_user_id, dm):
    '''
    Checks if a given user is in a given dm

    Arguments:
        auth_user_id    (int)    - ID of user of interest
        dm              (int)    - dm of interest

    Return Value:
        Returns True if user exists in dm
        Returns False if user does not exist in dm
    '''
    for user in dm['u_ids']:
        if (user == auth_user_id):
            return True
    return False

def user_is_not_original_creator(auth_user_id, dm_id):
    '''
    Checks if a user is the original creator of a dm

    Arguments:
        auth_user_id    (int)    - ID of user of interest
        dm_id           (int)    - id of dm

    Return Value:
        Returns True if user is owner in dm
        Returns False if user not owner in dm
    '''
    dm = find_dm(dm_id)
    if dm['owners_id'] == auth_user_id:
        return True
    return False

def find_user_in_dm_id(user_id, dm_id):
    '''
    Checks if a given user is in a given dm using dm_id

    Arguments:
        user_id    (int)    - ID of user of interest
        dm_id      (int)    - ID of dm of interest

    Return Value:
        Returns dictionary of user on condition user exists in dm
        Returns None on condition that user is not in the dm
    '''
    dm = find_dm(dm_id)
    if dm == None:
        return None
    members = dm['u_ids']
    for user in members:
        if user == user_id:
            return user
    return None

def check_if_owner_id(auth_user_id, dm_id):
    '''
    Checks if auth_user_id is an owner id is in a given dm

    Arguments:
        auth_user_id    (int)    - ID of user of interest
        dm_id             (int)    - id of dm of interest

    Return Value:
        Returns True if user is an owner
        Returns False if user is not
    '''
    dm = find_dm(dm_id)
    if dm is None:
        return False
    if auth_user_id == dm['owners_id']:
        return True
    return False

def count_seams_owners():
    '''
    Counts the total amount of seams owners in users from data store

    Return Value:
        Returns the amount of seams owners as an <int>
    '''
    store = data_store.get()
    users = store['users']
    owner_counter = 0
    for user in users:
        if user['permission_id'] == 1:
            owner_counter += 1
    return owner_counter

def count_messages_in_dm(dm_id):
    '''
    Counts the total amount of messages in a given dm

    Return Value:
        Returns the amount of messages in the DM as an <int>
    '''

    store = data_store.get()
    num_msgs_in_dm = 0
    dm_messages = store['dm_messages']
    for message in dm_messages:
        if message['dm_id'] == dm_id:
            num_msgs_in_dm +=1
    return num_msgs_in_dm


def store_history(auth_user_id, function, params=None):
    '''
    Stores the user id, function name and any additional parameters into data store 'history'

    Arguments:
        auth_user_id    (int)    - ID of user that launched the function
        function        (function) - the function that store_history is placed in
        params          (list) - List of any important parameters

    '''
    store = data_store.get()
    history = store['history']
    command_dict = {
        'command': function.__name__,
        'auth_user_id': auth_user_id,
        'timestamp': int(time.time()),
        'params': params
    }
    history.append(command_dict)
    store['history'] = history
    data_store.set(store)
    return
