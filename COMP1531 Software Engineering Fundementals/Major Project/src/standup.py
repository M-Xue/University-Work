import datetime
import threading
from src.error import AccessError, InputError
from src.security_helper import check_valid_token, decode_jwt
from src.helper import find_channel, find_user_in_channel_id, find_user
from src.message import message_send_v1
from src.data_store import data_store

def standup_start_v1(token, channel_id, length):
    '''
    Creates a new standup in the given channel that lasts the given length in seconds.

    Arguments:
        token       (string)    - token of an authorised user of Seams
        channel_id  (int)       - id of a channel in Seams
        length      (int)       - length (in seconds) of how long the standup will last for

    Exceptions:
        InputError  - Occurs when: > channel id is invalid
                                   > length is a negative integer
                                   > an active standup is currently running
        AccessError - Occurs when: > token is invalid
                                   > channel_id is valid and token is not a member of the channel

    Return Value:
        Returns {time_finish} on the condition that a new standup is created
    '''
    # Check if token is valid
    check_valid_token(token)
    # get auth_user_id from token
    auth_user_id = decode_jwt(token)['auth_user_id']

    # If channel_id is valid and auth_user is not a member of the channel
    if ((find_channel(channel_id) is not None) and
        (find_user_in_channel_id(auth_user_id, channel_id) is None)):
        raise AccessError(description='token is not a member of channel')

    # If channel_id is not a valid channel
    if find_channel(channel_id) is None:
        raise InputError(description='Invalid channel_id')

    # If length is a negative integer
    if length < 0:
        raise InputError(description='Invalid standup length')

    # If an active standup is currently running
    standup_status = find_channel(channel_id)['standup_active']
    if standup_status is True:
        raise InputError(description='Standup already active')
    
    # Start standup
    t = threading.Timer(length, standup_end, [token, channel_id])
    t.start()

    channel = find_channel(channel_id)
    time_finish = datetime.datetime.now() + datetime.timedelta(seconds=length)
    timestamp = int(time_finish.timestamp())

    channel['standup_active'] = True
    channel['standup_time_finish'] = timestamp
    channel['standup_start_user'] = auth_user_id
    channel['standup_messages'] = []

    return {'time_finish': timestamp}


def standup_active_v1(token, channel_id):
    '''
    Checks if a standup is active in the given channel.

    Arguments:
        token       (string)    - token of an authorised user of Seams
        channel_id  (int)       - id of a channel in Seams

    Exceptions:
        InputError  - Occurs when: > channel id is invalid

        AccessError - Occurs when: > token is invalid
                                   > channel_id is valid and token is not a member of the channel

    Return Value:
        Returns {is_active, time_finish} on the condition that a standup is successfully checked
    '''
    # Check if token is valid
    check_valid_token(token)
    # get auth_user_id from token
    auth_user_id = decode_jwt(token)['auth_user_id']

    # If channel_id is valid and auth_user is not a member of the channel
    if ((find_channel(channel_id) is not None) and
        (find_user_in_channel_id(auth_user_id, channel_id) is None)):
        raise AccessError(description='token is not a member of channel')

    # If channel_id is not a valid channel
    if find_channel(channel_id) is None:
        raise InputError(description='Invalid channel_id')

    # If an active standup is currently running
    standup_status = find_channel(channel_id)['standup_active']
    if standup_status is True:
        timestamp = find_channel(channel_id)['standup_time_finish']
        return {'is_active': True, 'time_finish': timestamp}
    else:
        return {'is_active': False, 'time_finish': None}

def standup_send_v1(token, channel_id, message):
    '''
    Buffers a message to the standup queue that will be sent after the standup ends

    Arguments:
        token       (string)    - token of an authorised user of Seams
        channel_id  (int)       - id of a channel in Seams
        message     (string)    - message that token wants to send

    Exceptions:
        InputError  - Occurs when: > channel id is invalid
                                   > length of message is over 1000 characters
                                   > no standup is currently runnning in channel

        AccessError - Occurs when: > token is invalid
                                   > channel_id is valid and token is not a member of the channel

    Return Value:
        Returns {} on the condition that a message was successfully added to the standup queue
    '''
    # Check if token is valid
    check_valid_token(token)
    # get auth_user_id from token
    auth_user_id = decode_jwt(token)['auth_user_id']

    # If channel_id is valid and auth_user is not a member of the channel
    if ((find_channel(channel_id) is not None) and
        (find_user_in_channel_id(auth_user_id, channel_id) is None)):
        raise AccessError(description='token is not a member of channel')

    # If channel_id is not a valid channel
    if find_channel(channel_id) is None:
        raise InputError(description='Invalid channel_id')

    # If message is over 1000 characters
    if len(message) > 1000:
        raise InputError(description='Message too long')

    # If an active standup is not currently running
    channel = find_channel(channel_id)
    standup_status = channel['standup_active']
    if standup_status is False:
        raise InputError(description='Standup not currently active')
    
    # Adds message to standup queue
    data = data_store.get()
    channel['standup_messages'].append({
        'u_id': auth_user_id,
        'message': message,
    })
    data_store.set(data)
    
    return {}

######## Standup helper functions ###########
def standup_end(token, channel_id):
    '''
    Ends the active standup session and sends messages

    Return Value:
    '''
    channel = find_channel(channel_id)
    messages = channel['standup_messages']
    if len(messages) != 0:
        package = ""
        for message in messages:
            handle_str = find_user(message['u_id'])['handle_str']
            package += handle_str + ": " + message['message'] + "\n"
        message_send_v1(token, channel_id, package)

    channel['standup_active'] = False
    channel['standup_time_finish'] = None
    channel['standup_start_user'] = None
    channel['standup_messages'] = []
