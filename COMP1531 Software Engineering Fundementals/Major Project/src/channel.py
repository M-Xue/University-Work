from src.data_store import data_store 
from src.notifications import *
from src.error import InputError, AccessError 
from src.helper import *
from src.security_helper import decode_jwt, check_valid_token
import copy
OWNERS_PERMISSION_ID = 1

def channel_invite_v1(auth_user_id, channel_id, u_id):
    '''
    Allows an authorised user to invite another user into a channel

    Arguments:
    auth_user_id    (int)    - id of an authorised user of Seams
    channel_id      (int)    - id of a channel in Seams
    u_id            (int)    - id of a user

    Exceptions:
    InputError  - Occurs when: > channel_id does not refer to a valid channel
                               > u_id does not refer to a valid user
                               > u_id refers to a user who is already a member of the channel

    AccessError - Occurs when: > channel_id is valid but auth_user_id is not a member of the channel

    Return Value:
    Returns {} on authorised user successfully inviting another user to the channel

    '''

    # If channel_id is not a valid channel
    if find_channel(channel_id) is None:
        raise InputError(description='Invalid channel_id')
    # If u_id is not a valid user
    if find_user(u_id) is None:
        raise InputError(description='Invalid u_id')
    # If u_id is already a member of the channel
    if find_user_in_channel_id(u_id, channel_id) is not None:
        raise InputError(description='u_id is already a member of channel')
    # If channel_id is valid and auth_user is not a member of the channel
    if ((find_channel(channel_id) is not None) and
        (find_user_in_channel_id(auth_user_id, channel_id) is None)):
        raise AccessError(description='auth_user_id is not a member of channel')

    store = data_store.get()
    # invited = store['users'][u_id] TODO potential bug, not sure! added line below instead
    store_history(u_id, channel_invite_v1)
    invited = find_user(u_id) 
    channel = find_channel(channel_id)
    channel['all_members'].append({
        'u_id': u_id,
        'email': invited['email'],
        'name_first': invited['name_first'],
        'name_last': invited['name_last'],
        'handle_str': invited['handle_str'],
    })
    data_store.set(store)

    #raise notifs when added to channel
    channel_add_notif(u_id, auth_user_id, channel_id)
    return {}

def channel_details_v1(auth_user_id, channel_id):
    '''
    Provides basic details of a channel that an authorised user is in.

    Arguments:
    auth_user_id    (int)    - ID of an authorised user
    channel_id      (int)    - id of a channel in

    Exceptions:
    InputError  - Occurs when: > channel_id does not refer to a valid channel

    AccessError - Occurs when: > channel_id is valid but auth_user_id is not a member of the channel
                               > auth_user_id does not refer to a valid user

    Return Value:
    Returns {name, is_public, owner_members, all_members} on the condition that all exceptions are passed

    '''
    
    # Check if given token is valid
    user = find_user(auth_user_id)
    
    # Find channel with matching ID
    channel = find_channel(channel_id)
    # If we successfully find a channel
    if channel != None:
        #If user is member of channel or global owner
        if find_user_in_channel(auth_user_id, channel) != None or user['permission_id'] == OWNERS_PERMISSION_ID:
            # Return dictionary of channel's details from data store
            return {'name': channel['name'], 'is_public': channel['is_public'], 'owner_members': channel['owner_members'], 'all_members': channel['all_members']}

        #If user is not in channel
        raise AccessError(description="Access Error, User does not belong to channel")
        
    # Could not find channel with specified channel ID
    else:
        raise InputError(description="Input Error, Channel ID does not exist")

def channel_messages_v1(auth_user_id, channel_id, start):
    """
        Given a channel with ID channel_id that the authorised user is a member of,
        return up to 50 messages between index "start" and "start + 50".

    Arguments:
        auth_user_id (int)    - <auth_user_id of user>
        channel_id   (int)    - <channel_id of channel>
        start        (int)    - <start of msg>

    Exceptions:
        InputError  - Occurs when channel_id does not refer to a valid channel
        InputError  - Occrus when start is greater than the total number of messages in the channel
        AccessError - Occurs when channel_id is valid and the authorised user is not a member of the channel

    Return Value:
        Returns {messages, start, end} on passing all exceptions
    """
    #get data_store to store in datas
    store = data_store.get()
    messages = store['messages']

    #inputerror when is not a valid channel
    if find_channel(channel_id) is None:
        raise InputError(description="channel_id does not refer to a valid channel")

    #access error when channel_id is valid and the authorised user is not a member of the channel
    if (find_channel(channel_id) is not None) and (find_user_in_channel_id(auth_user_id, channel_id) is None):
        raise AccessError(description="channel_id is valid and the authorised user is not a member of the channel")
        
    #input error when start is greater than the total number of messages in the channel
    tot_msg = 0
    for message in messages:
        #count number of msgs
        if message['channel_id'] == channel_id:
            tot_msg += 1
    #compare amount
    if tot_msg < start:
        raise InputError(description="start is greater than the total number of messages in the channel")

    #manage end value
    end = 0
    if tot_msg - start < 50:
        end = -1
    else:
        end = start + 50
    
    #reverse messages to start from the most recent
    #make a copy to not reverse it for other functions
    copy_store = copy.deepcopy(store)
    copy_store['messages'].reverse()

    index = 0
    msg_list = []
    for channel_message in copy_store['messages']:
        if channel_message['channel_id'] == channel_id:
            # if start <= index <= start +50
            if start <= index and index <= start + 49:
                #create dict
                message = {}
                #create copy of message['reacts']
                copy_reacts = channel_message.copy()
                for react in copy_reacts['reacts']:
                    if auth_user_id in react['u_ids']:
                        react.update({'is_this_user_reacted': True})
                    else:
                        react.update({'is_this_user_reacted': False})
                message['message_id'] = channel_message['message_id']
                message['u_id'] = channel_message['auth_user_id']
                message['message'] = channel_message['message']
                message['time_sent'] = channel_message['time_sent']
                message['reacts'] = copy_reacts['reacts']
                message['is_pinned'] = channel_message['is_pinned']
                msg_list.append(message)
            index += 1
    return {
        'messages': msg_list,
        'start': start,
        'end': end
    }

def channel_join_v1(auth_user_id, channel_id):
    '''
    Allows an authorised user to join a channel

    Arguments:
    auth_user_id    (int)    - id of an authorised user of Seams
    channel_id      (int)    - id of a channel in Seams

    Exceptions:
    InputError  - Occurs when: > channel_id does not refer to a valid channel
                               > auth_user_id refers to a user who is already a member of the channel

    AccessError - Occurs when: > channel_id is private and auth_user_id is not already a member of
                                 the channel and is not a Global Owner
                               > auth_user_id does not refer to a valid user

    Return Value:
    Returns {} on authorised user successfully joining the channel

    '''
    # If channel_id is not a valid channel
    if find_channel(channel_id) is None:
        raise InputError(description='Invalid channel_id')
    # If auth_user is already a member of the channel
    if find_user_in_channel_id(auth_user_id, channel_id) is not None:
        raise InputError(description='auth_user_id is already a member of channel')
    # If channel is private and auth_user is not already a member and is not a GLOBAL OWNER
    if ((is_channel_public(channel_id) == False) and
        (find_user_in_channel_id(auth_user_id, channel_id) == None)):
        if is_global_owner(auth_user_id) == False:
            raise AccessError(description='Channel is private, auth_user does not have permission to join')

    store = data_store.get()
    user = store['users'][auth_user_id]
    channel = find_channel(channel_id)
    channel['all_members'].append({
        'u_id': auth_user_id,
        'email': user['email'],
        'name_first': user['name_first'],
        'name_last': user['name_last'],
        'handle_str': user['handle_str']
    })
    store_history(auth_user_id, channel_join_v1)
    return {}

def channel_leave_v1(token, channel_id):
    '''
    Allows an authorised user to leave a channel

    Arguments:
    token       (string)    - token of an authorised user of Seams
    channel_id  (int)       - id of a channel in Seams

    Exceptions:
    InputError  - Occurs when channel_id does not refer to a valid channel

    AccessError - Occurs when: > channel_id is valid and token does not refer to an auth_user_id
                                 that is already a member of the channel
                               > token is not a valid token

    Return Value:
    Returns {} on authorised user successfully leaving the channel

    '''
    # If channel_id is not a valid channel
    if find_channel(channel_id) is None:
        raise InputError(description='Invalid channel_id')

    # Check if token is invalid
    check_valid_token(token)
    # get auth_user_id from token
    auth_user_id = decode_jwt(token)['auth_user_id']

    # If channel_id is valid and auth_user is not a member of the channel
    if ((find_channel(channel_id) is not None) and
        (find_user_in_channel_id(auth_user_id, channel_id) is None)):
        raise AccessError(description='token is not a member of channel')
    
    # If token is the starter of an active standup
    channel = find_channel(channel_id)
    if ((channel['standup_active'] is True) and
       (channel['standup_start_user'] == auth_user_id)):
        raise InputError(description='token is the starter of an active standup')
    
    # Remove user from all_members
    store = data_store.get()
    user = store['users'][auth_user_id]
    channel = find_channel(channel_id)
    channel['all_members'].remove({
        'u_id': auth_user_id,
        'email': user['email'],
        'name_first': user['name_first'],
        'name_last': user['name_last'],
        'handle_str': user['handle_str']
    })

    # Remove user from owners if they are an owner
    if user in channel['owner_members']:
        channel['owner_members'].remove({
        'u_id': auth_user_id,
        'email': user['email'],
        'name_first': user['name_first'],
        'name_last': user['name_last'],
        'handle_str': user['handle_str']
    })
    store_history(auth_user_id, channel_leave_v1)
    return {}

def channel_addowner_v1(token, channel_id, u_id):
    '''
    Allows an owner to make another member of the channel an owner

    Arguments:
    token           (string) - token of an authorised user of Seams
    channel_id      (int)    - id of a channel in Seams
    u_id            (int)    - id of a user

    Exceptions:
    InputError  - Occurs when: > channel_id does not refer to a valid channel
                               > u_id does not refer to a valid user
                               > u_id refers to a user who is not a member of the channel
                               > u_id refers to a user who is already an owner of the channel

    AccessError - Occurs when: > channel_id is valid but token is not a owner of the channel
                               > token is invalid

    Return Value:
    Returns {} on token successfully makinng another user a member of the channel

    '''
    # Check if token is invalid
    check_valid_token(token)
    # get auth_user_id from token
    auth_user_id = decode_jwt(token)['auth_user_id']

    # If channel_id is not a valid channel
    if find_channel(channel_id) is None:
        raise InputError(description='Invalid channel_id')
    # If channel_id is valid and token is not an owner of the channel
    if ((find_channel(channel_id) is not None) and
        (check_if_owner(auth_user_id, channel_id) is None)):
        # If token is not a member of the channel or is not a global member
        if ((find_user_in_channel_id(auth_user_id, channel_id) is None) or 
           (find_user(auth_user_id)['permission_id'] == 2)):
            raise AccessError(description='token is not an owner of the channel')
    # If u_id is not a valid user
    if find_user(u_id) is None:
        raise InputError(description='Invalid u_id')
    # If u_id is is not a member of the channel
    if find_user_in_channel_id(u_id, channel_id) is None:
        raise InputError(description='u_id is not a member of the channel')

    
    store = data_store.get()
    user = store['users'][u_id]
    channel = find_channel(channel_id)

    # If u_id is already an owner of the channel
    if (check_if_owner(u_id, channel_id)) is not None:
        raise InputError(description='u_id is already an owner of the channel')
    
    channel['owner_members'].append({
        'u_id': u_id,
        'email': user['email'],
        'name_first': user['name_first'],
        'name_last': user['name_last'],
        'handle_str': user['handle_str']
    })
    return {}

def channel_removeowner_v1(token, channel_id, u_id):
    '''
    Allows an owner to remove an owner of the channel

    Arguments:
    token           (string) - token of an authorised user of Seams
    channel_id      (int)    - id of a channel in Seams
    u_id            (int)    - id of a user

    Exceptions:
    InputError  - Occurs when: > channel_id does not refer to a valid channel
                               > u_id does not refer to a valid user
                               > u_id refers to a user who is not an owner of the channel
                               > u_id refers to a user who is currently the only owner of the channel

    AccessError - Occurs when: > channel_id is valid but token is not an owner of the channel
                               > token is invalid

    Return Value:
    Returns {} on token successfully makinng another user a member of the channel

    '''
    # Check if token is invalid
    check_valid_token(token)
    # get auth_user_id from token
    auth_user_id = decode_jwt(token)['auth_user_id']

    # If channel_id is valid and token is not an owner of the channel
    if ((find_channel(channel_id) is not None) and
        (check_if_owner(auth_user_id, channel_id) is None)):
        # If token is not a member of the channel or is not a global member
        if ((find_user_in_channel_id(auth_user_id, channel_id) is None) or 
           (find_user(auth_user_id)['permission_id'] == 2)):
            raise AccessError(description='token is not an owner of the channel')
    # If channel_id is not a valid channel
    if find_channel(channel_id) is None:
        raise InputError(description='Invalid channel_id')
    # If u_id is not a valid user
    if find_user(u_id) is None:
        raise InputError(description='Invalid u_id')
    # If u_id is is not an owner of the channel
    if check_if_owner(u_id, channel_id) is None:
        raise InputError(description='u_id is not an owner of the channel')
    
    
    store = data_store.get()
    user = store['users'][u_id]
    channel = find_channel(channel_id)

    # If u_id is the only owner of the channel
    if (check_if_owner(u_id, channel_id) is not None) and (len(channel['owner_members']) == 1):
        raise InputError(description='u_id is the only owner of the channel')
    
    channel['owner_members'].remove({
        'u_id': u_id,
        'email': user['email'],
        'name_first': user['name_first'],
        'name_last': user['name_last'],
        'handle_str': user['handle_str']
    })
    return {}
