from ctypes import util
from src.data_store import data_store 
from src.error import AccessError, InputError
from src.security_helper import decode_jwt, check_valid_token
from src.helper import find_user_in_channel, find_user_in_dms, count_messages_in_dm

# REMEMBER TO ADD MESSAGE SEND LATER

def user_stats_v1(token):
    '''
    Given the token a valid user, returns timestamped information about the amount of channels and DMS they have joined,
    as well as the amount of messages they have sent. Their involvement rate in SEAMS is also returned in the stats.

    Arguments:
        token  (str) -  token of an valid user

    Exceptions:
        AccessError - Occurs when:
                        - Token of a user is invalidated
    Return Value:
        Returns a dictionary of the user's stats
    '''
    check_valid_token(token)
    user_id = decode_jwt(token)['auth_user_id']
    time_registered = 0
    num_channels_joined = 0
    num_dms_joined = 0
    num_messages_sent = 0

    store = data_store.get()
    history = store['history']
    
    # Find the time when the user got registered
    for commands in history:
        if commands['auth_user_id'] == user_id and commands['command'] == 'auth_register_v1':
            time_registered = commands['timestamp']
            break

    # Create dict with time registered
    user_stats = {
        'channels_joined': [{'num_channels_joined': 0, 'time_stamp': time_registered}],
        'dms_joined': [{'num_dms_joined': 0, 'time_stamp': time_registered}],
        'messages_sent': [{'num_messages_sent': 0, 'time_stamp': time_registered}],
        'involvement_rate': 0,
    }

    for commands in history:
        # Append to stats when user is in a channel
        if commands['auth_user_id'] == user_id and commands['command'] in ['channel_join_v1', 'channels_create_v1']:
            num_channels_joined += 1
            mydict = {'num_channels_joined': num_channels_joined, 'time_stamp': commands['timestamp']}
            user_stats['channels_joined'].append(mydict)

        # Append to stats when user is invited into a channel
        elif commands['auth_user_id'] == user_id and commands['command'] == 'channel_invite_v1':
            num_channels_joined += 1
            mydict = {'num_channels_joined': num_channels_joined, 'time_stamp': commands['timestamp']}
            user_stats['channels_joined'].append(mydict)

        # Append to stats when user leaves a channel
        elif commands['auth_user_id'] == user_id and commands['command'] == 'channel_leave_v1':
            num_channels_joined -= 1
            mydict = {'num_channels_joined': num_channels_joined, 'time_stamp': commands['timestamp']}
            user_stats['channels_joined'].append(mydict)

        # Append to stats when user creates a dm or is invited into a DM
        elif commands['auth_user_id'] == user_id and commands['command'] == 'dm_create_v1' or (commands['command'] == 'dm_create_v1' and user_id in commands['params']):
            num_dms_joined += 1
            mydict = {'num_dms_joined': num_dms_joined, 'time_stamp': commands['timestamp']}
            user_stats['dms_joined'].append(mydict)

        # Append to stats when user leaves a dm
        elif commands['auth_user_id'] == user_id and commands['command'] == 'dm_leave_v1':
            num_dms_joined -= 1
            mydict = {'num_dms_joined': num_dms_joined, 'time_stamp': commands['timestamp']}
            user_stats['dms_joined'].append(mydict)

        # Append to stats when user sends or shares a message
        elif commands['auth_user_id'] == user_id and commands['command'] in ['message_send_v1', 'message_senddm_v1', 'message_share_v1', 'message_sendlater_v1', 'message_sendlaterdm_v1']:
            num_messages_sent += 1
            mydict = {'num_messages_sent': num_messages_sent, 'time_stamp': commands['timestamp']}
            user_stats['messages_sent'].append(mydict)
        
        

    channels = store['channels']
    dms = store['dms']
    messages = store['messages']
    dm_messages = store['dm_messages']

    sum_1 = num_channels_joined + num_dms_joined + num_messages_sent
    sum_2 = len(channels) + len(dms) + len(messages) + len(dm_messages)
    if sum_2 == 0:
        user_stats['involvement_rate'] = 0
        return {'user_stats': user_stats}
    
    involvement_rate = sum_1 / sum_2

    if involvement_rate > 1:
        involvement_rate = 1
    user_stats['involvement_rate'] = involvement_rate
    return {'user_stats': user_stats}


def users_stats_v1(token):
    '''
    Given a valid token, returns information about the usage and effectiveness of the workspace in the form of a timestamped dictionary.

    Arguments:
        token  (str) -  token of an valid user

    Exceptions:
        AccessError - Occurs when:
                        - Token of a user is invalidated
    Return Value:
        Returns a dictionary of the workspace stats
    '''

    check_valid_token(token)
    
    store = data_store.get()
    history = store['history']
    users = store['users']
    channels = store['channels']
    dms = store['dms']

    num_users = len(users)
    num_channels_exist = 0
    num_dms_exist = 0
    num_messages_exist = 0

    # Get timestamp from when the first user is registered into the server
    first_timestamp = history[0]['timestamp']

    workspace_stats = {
        "channels_exist": [{'num_channels_exist': 0, 'time_stamp': first_timestamp}],
        "dms_exist": [{'num_dms_exist': 0, 'time_stamp': first_timestamp}],
        "messages_exist": [{'num_messages_exist': 0, 'time_stamp': first_timestamp}],
        "utilization_rate": 0
    }

    for commands in history:
        if commands['command'] == 'channels_create_v1':
            num_channels_exist += 1
            mydict = {'num_channels_exist': num_channels_exist, 'time_stamp': commands['timestamp']}
            workspace_stats['channels_exist'].append(mydict)

        elif commands['command'] == 'dm_create_v1':
            num_dms_exist += 1
            mydict = {'num_dms_exist': num_dms_exist, 'time_stamp': commands['timestamp']}
            workspace_stats['dms_exist'].append(mydict)
        
        elif commands['command'] == 'dm_remove_v1':
            num_dms_exist -= 1
            mydict = {'num_dms_exist': num_dms_exist, 'time_stamp': commands['timestamp']}
            workspace_stats['dms_exist'].append(mydict)

            # Append to stats the amount of messages that were lost from the removed DM
            num_messages_exist -= count_messages_in_dm(commands['params'][0])
            mydict1 = {'num_messages_exist': num_messages_exist, 'time_stamp': commands['timestamp']}
            workspace_stats['messages_exist'].append(mydict1)
        
        elif commands['command'] in ['message_send_v1', 'message_senddm_v1', 'message_share_v1','message_sendlater_v1', 'message_sendlaterdm_v1']:
            num_messages_exist += 1
            mydict = {'num_messages_exist': num_messages_exist, 'time_stamp': commands['timestamp']}
            workspace_stats['messages_exist'].append(mydict)
        
        elif commands['command'] == 'message_remove_v1':
            num_messages_exist -= 1
            mydict = {'num_messages_exist': num_messages_exist, 'time_stamp': commands['timestamp']}
            workspace_stats['messages_exist'].append(mydict)
        
        elif commands['command'] == 'admin_user_remove_v1':
            num_users -= 1



    num_users_who_have_joined_at_least_one_channel_or_dm = 0
    for user in users:
        # Loop through channels to see if they are in at least one channel
        joined_channel = False
        for channel in channels:
            if find_user_in_channel(user['u_id'], channel) != None:
                num_users_who_have_joined_at_least_one_channel_or_dm += 1
                joined_channel = True
                break
    
        # If they weren't in any channels, check if they are in any dms
        for dm in dms:
            if find_user_in_dms(user['u_id'], dm) != None and joined_channel is False:
                num_users_who_have_joined_at_least_one_channel_or_dm += 1
                break
    
    

    utilization_rate = num_users_who_have_joined_at_least_one_channel_or_dm / num_users
    workspace_stats['utilization_rate'] = utilization_rate

    return {'workspace_stats': workspace_stats}
