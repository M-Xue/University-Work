from src.data_store import data_store
from src.error import AccessError
from src.helper import *
from src.security_helper import *


def notifications_get_v1(token):
    '''
    Return the user's most recent 20 notifications

    Arguments:
        token (string) - authorised user

    Exceptions:
        AccessError - occurs when token passed in is invalid

    Return Value: {notifications}
    '''
    # Check if token is invalid
    check_valid_token(token)

    auth_user_id = decode_jwt(token)['auth_user_id']
    user = find_user(auth_user_id)
    notifs_list = user['notifications']

    #Return the user's most recent 20 notifications,
    #ordered from most recent to least recent.        
    if len(notifs_list) >= 20:
        notifications = notifs_list[::-1][0:20]
    else:
        notifications = notifs_list[::-1]
                
    return notifications

###helper function###
def channel_add_notif(added_id, inviter_id, channel_id):
    '''
    appends to notifs when the user is added to channel

    Arguments:
        added_id (int) - id of added user
        inviter_id (int) - id of inviting user
        channel_id (int) - id of channel that the user got invited

    Return Value
    '''
    added_user = find_user(added_id)
    inviter = find_user(inviter_id)
    channel = find_channel(channel_id)

    added_user['notifications'].append({
        "channel_id": channel_id,
        "dm_id": -1,
        "notification_message": f"{inviter['handle_str']} added you to {channel['name']}"
    })
    return

def channel_add_notif_dm(added_user_id, inviter_id, dm_id):
    '''
    appends to notifs when the user is added to dm

    Arguments:
        added_user_id (int) - id of added user
        inviter_id (int) - id of inviting user
        dm_id (int) - id of dm that the user got invited

    Return Value
    '''
    added_user = find_user(added_user_id)
    inviter = find_user(inviter_id)
    dm = find_dm(dm_id)

    assert added_user != None
    assert inviter != None
    assert dm != None

    added_user['notifications'].append({
        "channel_id": -1,
        "dm_id": dm_id,
        "notification_message": f"{inviter['handle_str']} added you to {dm['name']}"
    })
    return

def send_message_notif_channel(sender_id, channel_id, message):
    '''
    appends to notifs when the user is tagged in the message that is sent

    Arguments:
        sender_id (int) - id of sending user
        channel_id (int) - id of channel that the msg got sent
        message (string) - content of message that is sent

    Return Value
    '''
    if len(message) >= 20:
        snippet = message[0:20]
    else:
        snippet = message
    
    channel = find_channel(channel_id)
    channel_name = channel['name']

    sender_handle = find_user(sender_id)['handle_str']

    for member in channel['all_members']:
        user_id = member['u_id']
        user = find_user(user_id)
        handle = user['handle_str']
        if f"@{handle}" in message:
            if find_user_in_channel_id(user_id, channel_id) is not None:
                user['notifications'].append({
                    'channel_id': channel_id,
                    'dm_id': -1,
                    'notification_message': f"{sender_handle} tagged you in {channel_name}: {snippet}",
                })

def send_dm_notif_dm(sender_id, dm_id, message):
    '''
    appends to notifs when the user is tagged in the dm that is sent

    Arguments:
        sender_id (int) - id of sending user
        dm_id (int) - id of dm that the msg got sent
        message (string) - content of message that is sent

    Return Value
    '''
    if len(message) >= 20:
        snippet = message[0:20]
    else:
        snippet = message         

    dm = find_dm(dm_id)
    dm_name = dm['name']

    sender_handle = find_user(sender_id)['handle_str']

    for member in dm['u_ids']:
        user_id = member
        user = find_user(user_id)
        handle = user['handle_str']
        if f"@{handle}" in message:
            if find_user_in_dm_id(user_id, dm_id) is not None:
                user['notifications'].append({
                    'channel_id': -1,
                    'dm_id': dm_id,
                    'notification_message': f"{sender_handle} tagged you in {dm_name}: {snippet}"
                })

def react_notif(reactor_id, message_id):
    '''
    appends to notifs when the user is reacts to the message that is sent

    Arguments:
        reactor_id (int) - id of reacting user
        message_id (int) - id of message that got sent

    Return Value
    '''
    reactor_handle = find_user(reactor_id)['handle_str']
    channel_id = -1
    dm_id = -1

    message_channel_check = find_message_in_channel(message_id)
    message_dm_check = find_message_in_dm(message_id)

    if message_channel_check is not None:
        channel_id = message_channel_check['channel_id']
        channel_name = find_channel(channel_id)['name']
        message_send_id = message_channel_check['auth_user_id']
        message_send_user = find_user(message_send_id)
        if find_user_in_channel_id(message_send_id, channel_id) is not None:
            message_send_user['notifications'].append({
                'channel_id': channel_id,
                'dm_id': dm_id,
                'notification_message': f"{reactor_handle} reacted to your message in {channel_name}",
            })
    elif message_dm_check is not None:
        dm_id = message_dm_check['dm_id']
        dm_name = find_dm(dm_id)['name']
        message_send_id = message_dm_check['auth_user_id']
        message_send_user = find_user(message_send_id)
        if find_user_in_dm_id(message_send_id, dm_id) is not None:
            message_send_user['notifications'].append({
                'channel_id': channel_id,
                'dm_id': dm_id,
                'notification_message': f"{reactor_handle} reacted to your message in {dm_name}",
            })
