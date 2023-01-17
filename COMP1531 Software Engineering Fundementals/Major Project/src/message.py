import datetime
from src.data_store import data_store
from src.error import InputError, AccessError
from src.msg_error_helper import *
from src.helper import *
from src.notifications import *
from src.security_helper import *
from src.get_msg_id_helper import *
import time

INVALID = -1
OWNERS_PERMISSION_ID = 1

def message_send_v1(token, channel_id, message):
    '''
    <Send a message from the authorised user to the channel specified by channel_id.>

    Arguments:
        token      (String)    - <token of the user that is sending message>
        channel_id (Int)       - <id of the channel where the message is being sent>
        message    (String)    - <message>    
        
        
    Exceptions:
        InputError  - occurs when channel_id does not refer to a valid channel
        InputError  - occurs when length of message is less than 1 or over 1000 characters
        AccessError - occurs when channel_id is valid and the authorised user is not a member of the channel
        AccessError - occurs when token passed in is invalid

    Return Value:
        Returns { message_id } when passing all exceptions
    '''
    store = data_store.get()

    #input error when channel_id is invalid
    valid_channel = check_valid_channel(channel_id)
    if valid_channel == False:
        raise InputError(description='channel_id does not refer to a valid channel')

    # Check if token is invalid, if not raise access error 
    check_valid_token(token)
    # get auth_user_id from token
    auth_user_id = decode_jwt(token)['auth_user_id']

    # If channel_id is valid and auth_user is not a member of the channel
    if ((find_channel(channel_id) is not None) and
        (find_user_in_channel_id(auth_user_id, channel_id) is None)):
        raise AccessError(description='auth_user is not a member of the channel')

    #raise input error when length of message is less than 1 or over 1000 characters
    len_msg = len(message)
    if len_msg < 1 or len_msg > 1000:
        raise InputError('length of message is less than 1 or over 1000 characters')

    #msg_id starts from 0
    msg_id = get_msg_id()
    new_message ={
            'auth_user_id': auth_user_id,
            'message': message,
            'time_sent': int(datetime.datetime.utcnow()
                            .replace(tzinfo= datetime.timezone.utc).timestamp()),
            'channel_id':  channel_id,
            'dm_id': None,
            'message_id': msg_id,
            'reacts': [
                {
               'react_id': 0,
               'u_ids': []
                }
            ],
            'is_pinned': False,
    }

    store['messages'].append(new_message)

    #raise notifs when tagged
    send_message_notif_channel(auth_user_id, channel_id, message)

    data_store.set(store)
    store_history(auth_user_id, message_send_v1)
    return {
        'message_id': msg_id
    }

def message_remove_v1(token, message_id):
    '''
    Allows a SEAMS owner or creator of the channel/DM to remove a message by their MSG ID

    Arguments:
    token           (string) - token of an authorised user of Seams
    message_id      (int)    - id of a message

    Exceptions:
    InputError  - Occurs when: > message_id does not refer to a valid message in a channel/dm that the user is in

    AccessError - Occurs when: > the user isn't an owner of the channel/dm or a seams owner
                               > the user isn't the owner of the message sent
                               > the given token is invalid

    Return Value:
    Returns {} on token successfully removing specified message

    '''

    store = data_store.get()
    messages = store['messages']
    dm_messages = store['dm_messages']
    check_valid_token(token)
    user_id = decode_jwt(token)['auth_user_id']
    user = find_user(user_id)

    store_history(user_id, message_remove_v1)

    # Obtain message from message_id, raises InputError if ID doesn't exist
    message = find_message(message_id)

     # Check if user is in that channel
    if find_user_in_channel_id(user['u_id'], message['channel_id']) != None:
        channel = find_channel(message['channel_id'])
        # If user is creator of channel or user is seams owner or sender of message being removed
        if user['permission_id'] == OWNERS_PERMISSION_ID or user['u_id'] == message['auth_user_id'] or is_user_channel_owner(user, channel) == True:
            messages.remove(message)
            data_store.set(store)
            return {}

        # User doesn't have permissions in channel/dm to remove message
        raise AccessError(description="User doesn't have authorisation to remove message in channel")

    # Check if user is in DM
    elif find_user_in_dm_id(user['u_id'], message['dm_id']) != None:
        dm = find_dm(message['dm_id'])
        # Check if user has permissions to remove message
        if user['u_id'] == message['auth_user_id'] or user['u_id'] == dm['owners_id']:
            dm_messages.remove(message)
            data_store.set(store)
            return {}
    
        # User doesn't have permissions in channel/dm to remove message   
        raise AccessError(description="User doesn't have authorisation to remove message in DM")

    # User is not in DM/channel
    raise InputError(description="User is not in specified DM/Channel")

def message_edit_v1(token, message_id, message): 
    '''
    <Given a message, update its text with new text. If the new message is an empty string, the message is deleted.>

    Arguments:
        <token>      (String)    - <token of the user>
        <message_id> (Int)       - <the id of the message>
        <message>    (String)    - <new text message>
    
    Exceptions:
        InputError - ocrrusw when length of message is over 1000 characters
        InputError  - occurs when message_id does not refer to a valid message within a channel/DM that the authorised user has joined
        AccessError - when message_id refers to a valid message in a joined channel/DM and none of the following are true:
                        -the message was sent by the authorised user making this request
                        -the authorised user has owner permissions in the channel/DM
        AccessError - occurs when token passed in is invalid

    Return Value:
        {}
    '''
    #access error when token is invalid
    check_valid_token(token)

    store = data_store.get()

    #input error when invalid message id
    valid_msg = msg_id_valid(message_id)
    if valid_msg == False:
        raise InputError(description='message_id does not refer to a valid message within a channel/DM that the authorised user has joined')
    #access when error when sent from user with no permission
    #first if statement checks whether its sent by authorised user
    sent_from_user = False 
    msg = return_message_is_valid(message_id)
    auth_user_id = decode_jwt(token)['auth_user_id']
    if auth_user_id == msg['auth_user_id']:
        sent_from_user = True 

    #checks if user has permission
    has_owner_permission = False
    if msg['channel_id'] != INVALID:
        has_owner_permission = check_user_has_channel_permission(msg['channel_id'], auth_user_id)
    else:
        has_owner_permission = check_user_has_dm_permission(msg['dm_id'], auth_user_id)

    #throws access error when is not sent from authorised user or user doesnt have permission
    access_error = sent_from_user or has_owner_permission
    if not access_error:
        raise AccessError(description="the message wasn't sent by the authorised user making this request & the authorised user doesn't have owner permissions in the channel/DM")

    #input error when message is over 1000 chars
    if len(message) > 1000:
        raise InputError(description='length of message is over 1000 characters')

    #message is deleted when empty string
    if len(message) == 0:
        return message_remove_v1(token, message_id)

    #message is replaced with edited message
    for msg in store['messages']:
        if msg['message_id'] == message_id:
            msg['message'] = message
            break 

    return {}

def message_senddm_v1(token, dm_id, message):
    '''
    Sends a message from authorised_user to the DM specified by dm_id. 

    Parameters:
        token (str): authorised user
        dm_id (int): DM's id
        message (str): message sent

    Return value:
        message_id (dict): the message id with type { message_id }.
    '''
    
    # Check if token is invalid
    check_valid_token(token)
    # get auth_user_id from token
    auth_user_id = decode_jwt(token)['auth_user_id']

    # If dm_id not valid
    if find_dm(dm_id) is None:
        raise InputError(description='dm_id is invalid')

    # check message length
    if len(message) > 1000:
        raise InputError(description='Message cannot more than 1000 characters')
    if len(message) < 1:
        raise InputError(description='Message cannot be less than 1 character')
    

    # If dm_id is valid and auth_user is not a member of the dm
    if not check_if_owner_id(auth_user_id, dm_id) and not find_user_in_dms(auth_user_id, find_dm(dm_id)):
        raise AccessError(description="dm_id is valid and the authorised user is not a member of the DM")

    store = data_store.get()

    #get msg id from helper
    message_id = get_msg_id()

    #append new messages
    new_msg = {
        'message_id' : message_id,
        'auth_user_id': auth_user_id,
        'message': message,
        'time_sent': int(datetime.datetime.utcnow()
                            .replace(tzinfo= datetime.timezone.utc).timestamp()),
        'channel_id': -1,
        'dm_id': dm_id,
        'reacts': [
            {
            'react_id': 0,
            'u_ids': []
            }
        ],
        'is_pinned': False,
    }
    store['dm_messages'].append(new_msg)

    #raise notifs when tagged
    send_dm_notif_dm(auth_user_id, dm_id, message)

    store_history(auth_user_id, message_senddm_v1)
    return {
        'message_id': message_id
    }

def message_sendlater_v1(token, channel_id, message, time_sent):
    '''
    Send a message from the authorised user to the channel specified by channel_id 
    automatically at a specified time in the future.  

    Parameters:
        token (str): authorised user
        channel_id (int): channel's id
        message (str): message sent
        time_sent (datetime) : time when message is sent 

    Returns value:
        message_id (dict): the message id with type { message_id }.
    '''

    #input error when channel_id is invalid
    valid_channel = check_valid_channel(channel_id)
    if valid_channel == False:
        raise InputError(description='channel_id does not refer to a valid channel')

    # Check if token is invalid, if not raise access error 
    check_valid_token(token)
    # get auth_user_id from token
    auth_user_id = decode_jwt(token)['auth_user_id']

    # If channel_id is valid and auth_user is not a member of the channel
    if ((find_channel(channel_id) is not None) and
        (find_user_in_channel_id(auth_user_id, channel_id) is None)):
        raise AccessError(description='auth_user is not a member of the channel')

    #raise input error when length of message is less than 1 or over 1000 characters
    len_msg = len(message)
    if len_msg < 1 or len_msg > 1000:
        raise InputError(description='length of message is less than 1 or over 1000 characters')

    #raise input error when time sent is in the past
    curr_time = int(datetime.datetime.now().timestamp())

    if curr_time > time_sent:
        raise InputError(description='Time sent is in the past')

    time.sleep(time_sent - curr_time)
    store_history(auth_user_id, message_sendlater_v1)
    return message_send_v1(token, channel_id, message)

def message_sendlaterdm_v1(token, dm_id, message, time_sent):
    '''
    Send a message from the authorised user to the DM specified by dm_id 
    automatically at a specified time in the future. 

    Parameters:
        token (str): authorised user
        dm_id (int): DM's id
        message (str): message sent
        time_sent (datetime) : time when message sent

    Returns value:
        message_id (dict): the message id with type { message_id }.
    '''
    # Check if token is invalid
    check_valid_token(token)
    # get auth_user_id from token
    auth_user_id = decode_jwt(token)['auth_user_id']

    # raise input error if dm_id not valid
    if find_dm(dm_id) is None:
        raise InputError(description='dm_id is invalid')

    # check message length
    # raise input error when length of message is more than 1k or less than 1
    if len(message) > 1000:
        raise InputError(description='Message cannot more than 1000 characters')
    if len(message) < 1:
        raise InputError(description='Message cannot be less than 1 character')
    

    # raise access error if dm_id is valid and auth_user is not a member of the dm
    if not check_if_owner_id(auth_user_id, dm_id) and not find_user_in_dms(auth_user_id, find_dm(dm_id)):
        raise AccessError(description="dm_id is valid and the authorised user is not a member of the DM")

    #raise input error when time sent is in the past
    curr_time = int(datetime.datetime.now().timestamp())

    if curr_time > time_sent:
        raise InputError(description='Time sent is in the past')

    time.sleep(time_sent - curr_time)
    store_history(auth_user_id, message_sendlaterdm_v1)
    return message_senddm_v1(token, dm_id, message)

def message_react_v1(token, message_id, react_id):
    '''
    <Given a message within a channel or DM the authorised user is part of, add a "react" to that particular message.>

    Arguments:
        <token>      (String)    - <token of the user>
        <message_id> (Int)       - <the id of the message>
        <react_id>   (Int)       - <the id of a react>

    Exceptions:
        InputError - message_id is not a valid message within a channel or DM that the authorised user has joined
        InputError - react_id is not a valid react ID - currently, the only valid react ID the frontend has is 1
        InputError - the message already contains a react with ID react_id from the authorised user
        AccessError - occurs when token passed in is invalid

    Return Value:
        {}
    '''
    store = data_store.get()

    #access error when token is invalid
    check_valid_token(token)

    # get auth_user_id from token
    auth_user_id = decode_jwt(token)['auth_user_id']

    # InputError: message_id is invalid
    valid_msg = msg_id_valid(message_id)
    if valid_msg == False:
        raise InputError(description = 'message_id does not refer to a valid message within a channel/DM that the authorised user has joined')
    # InputError: react_id is invalid
    if react_id != 1:
        raise InputError(description = 'react_id is invalid')


    # InputError: message already contains a react
    message = find_message(message_id)
    if message['reacts'][0]['react_id'] == 1:
        if auth_user_id in message['reacts'][0]['u_ids']:
            raise InputError(description = "message already contains a react")

    # Functionality
    # Reacting a message
    message['reacts'][0]['react_id'] = 1
    message['reacts'][0]['u_ids'].append(auth_user_id)

    #raise notifs when the message gets reacted
    react_notif(auth_user_id, message_id)

    data_store.set(store)
    return {}

def message_unreact_v1(token, message_id, react_id):
    '''
    <Given a message within a channel or DM the authorised user is part of, remove a "react" to that particular message.>

    Arguments:
        <token>      (String)    - <token of the user>
        <message_id> (Int)       - <the id of the message>
        <react_id>   (Int)       - <the id of a react>

    Exceptions:
        InputError - message_id is not a valid message within a channel or DM that the authorised user has joined
        InputError - react_id is not a valid react ID - currently, the only valid react ID the frontend has is 1
        InputError - the message does not a react with ID react_id from the authorised user
        AccessError - occurs when token passed in is invalid

    Return Value:
        {}
    '''
    store = data_store.get()

    #access error when token is invalid
    check_valid_token(token)

    # get auth_user_id from token
    auth_user_id = decode_jwt(token)['auth_user_id']

    # InputError: message_id is invalid
    valid_msg = msg_id_valid(message_id)
    if valid_msg == False:
        raise InputError(description = 'message_id does not refer to a valid message within a channel/DM that the authorised user has joined')
    # InputError: react_id is invalid
    if react_id != 1:
        raise InputError(description = 'react_id is invalid')

    # InputError: message does not contain a react
    message = find_message(message_id)
    if auth_user_id not in message['reacts'][0]['u_ids']:
        raise InputError(description = "message does not contain a react")

    # Functionality - Unreacting a message
    message['reacts'][0]['react_id'] = 1
    message['reacts'][0]['u_ids'].remove(auth_user_id)

    data_store.set(store)
    return {}

def message_pin_v1(token, message_id):
    '''
    <Given a message within a channel or DM, mark it as "pinned".>

    Arguments:
        <token>      (String)    - <token of the user>
        <message_id> (Int)       - <the id of the message>

    Exceptions:
        InputError - message_id is not a valid message within a channel or DM that the authorised user has joined
        InputError - the message is already pinned
        AccessError - occurs when token passed in is invalid
        Accesserror - message_id refers to a valid message in a joined channel/DM and the authorised user does not have owner permissions in the channel/DM

    Return Value:
        {}
    '''
    store = data_store.get()
    #access error when token is invalid
    check_valid_token(token)

    # get auth_user_id from token
    auth_user_id = decode_jwt(token)['auth_user_id']

    # InputError: message_id is invalid
    valid_msg = msg_id_valid(message_id)
    if valid_msg == False:
        raise InputError(description = 'message_id does not refer to a valid message within a channel/DM that the authorised user has joined')

    # InputError: message already pinned
    message = find_message(message_id)
    if message['is_pinned'] == True:
        raise InputError(description = "message is already pinned")

    # If message_id is valid and auth_user is not a member of the dm/channel
    if find_user_in_channel_id(auth_user_id, message['channel_id']) == None and find_user_in_dm_id(auth_user_id, message['dm_id']) == None:
        raise InputError(description="message_id is valid and the authorised user is not a member of the Channel/DM")

    # message_id refers to a valid message in a joined channel/DM and the authorised user does not have owner permissions in the channel/DM
    if check_if_owner(auth_user_id, message['channel_id']) == None and check_if_owner_id(auth_user_id, message['dm_id']) == False:
        if is_global_owner(auth_user_id) == False:
            raise AccessError(description="message_id is valid and authorise user does not have owner permissions in the channel/DM")

    # Functionality
    # Pinning a message
    message['is_pinned'] = True

    data_store.set(store)
    return {}

def message_unpin_v1(token, message_id):
    '''
    <Given a message within a channel or DM, mark it as "pinned".>

    Arguments:
        <token>      (String)    - <token of the user>
        <message_id> (Int)       - <the id of the message>

    Exceptions:
        InputError - message_id is not a valid message within a channel or DM that the authorised user has joined
        InputError - the message is not pinned
        AccessError - occurs when token passed in is invalid
        Accesserror - message_id refers to a valid message in a joined channel/DM and the authorised user does not have owner permissions in the channel/DM

    Return Value:
        {}
    '''

    store = data_store.get()
    #access error when token is invalid
    check_valid_token(token)

    # get auth_user_id from token
    auth_user_id = decode_jwt(token)['auth_user_id']

    # InputError: message_id is invalid
    valid_msg = msg_id_valid(message_id)
    if valid_msg == False:
        raise InputError(description = 'message_id does not refer to a valid message within a channel/DM that the authorised user has joined')

    # InputError: message is not pinned
    message = find_message(message_id)
    if message['is_pinned'] == False:
        raise InputError(description = "message is not pinned")

    # If message_id is valid and auth_user is not a member of the dm/channel
    if find_user_in_channel_id(auth_user_id, message['channel_id']) == None and find_user_in_dm_id(auth_user_id, message['dm_id']) == None:
        raise InputError(description="message_id is valid and the authorised user is not a member of the Channel/DM")

    # message_id refers to a valid message in a joined channel/DM and the authorised user does not have owner permissions in the channel/DM
    if check_if_owner(auth_user_id, message['channel_id']) == None and check_if_owner_id(auth_user_id, message['dm_id']) == False:
        if is_global_owner(auth_user_id) == False:
            raise AccessError(description="message_id is valid and authorise user does not have owner permissions in the channel/DM")

    # Functionality
    # Unpinning a message
    message['is_pinned'] = False

    data_store.set(store)
    return {}

def message_share_v1(token, og_message_id, message, channel_id, dm_id):
    '''
    Given the message ID of an existing message, sends the contents of that original message + an additional message
    to a specified channel/dm.

    Arguments:
        <token> (str) - <token of user>
        <og_message_id>  (int) - <ID of original message>
        message (str) - <optional message in addition to the shared message>
        channel_id (int) - <ID of channel that message being shared>
        dm_id (int) - <Id of dm>
        ...

    Exceptions:
        AccessError - when token is invalid 
        AccessError - the pair of channel_id and dm_id are valid (i.e. one is 
                      -1, the other is valid) and the authorised user has not 
                      joined the channel or DM they are trying to share the 
                      message to
        InputError  - both channel_id and dm_id are invalid
        InputError  - neither channel_id nor dm_id are -1
        InputError  - og_message_id does not refer to a valid message within a 
                      channel/DM that the authorised user has joined
        InputError  - length of message is more than 1000 characters          

    Return Value: {'shared_message_id': message_id}    
    '''
    
    check_valid_token(token)
    og_message = find_message(og_message_id)
    user_id = decode_jwt(token)['auth_user_id']

    # Check if user is not in channel/DM that the message is being shared to
    if( (channel_id == -1 and find_user_in_dm_id(user_id, dm_id) == None) or (find_user_in_channel_id(user_id, channel_id) == None and dm_id == -1) ):
        raise AccessError(description='User must be part of DM/Channel that the message is being shared to')

    if channel_id != -1 and dm_id != -1:
        raise InputError(description="No -1")

    # Check if user is not in the channel/dm of OG message
    if find_user_in_channel_id(user_id, og_message['channel_id']) == None and find_user_in_dm_id(user_id, og_message['dm_id']) == None:
        raise InputError(description='User must be part of channel/DM of OG message')
    
    if len(message) > 1000:
        raise InputError(description='Message is over 1000 Characters')
    
    ### All Error Checking Completed
    store_history(user_id, message_share_v1)
    # Functionality
    # Sharing message to a channel
    if dm_id == -1:
        shared_message = og_message['message'] + ' | ' + message
        shared_message_id = message_send_v1(token, channel_id, shared_message)['message_id']
        return {'shared_message_id': shared_message_id}
    # Sharing message to a DM
    else:
        shared_message = og_message['message'] + ' | ' + message
        shared_message_id = message_senddm_v1(token, dm_id, shared_message)['message_id']
        return {'shared_message_id': shared_message_id}
