import pytest
import requests
from src import config
CHANNEL_NAME = "Name"
CHANNEL_IS_PUBLIC = True
MESSAGE = "Hello World!"
LONG_MESSAGE = "Too Long!" * 1000
SUCCESS = 200
INPUT_ERROR = 400
ACCESS_ERROR = 403
NONEXISTANT_MSG_ID = 11111

@pytest.fixture(autouse=True)
def clear():
    requests.delete(config.url + "/clear/v1")

@pytest.fixture
def register_user1():
    token_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    return token_http

@pytest.fixture
def register_user2():
    token2_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example2@gmail.com',
        'password': 'password',
        'name_first': 'Dave',
        'name_last': 'Cave'})
    return token2_http

def test_message_share_channel_to_channel(register_user1):
    user_req = register_user1
    token = user_req.json()['token']

    # Create channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # Send a Message in created channel
    send_http = requests.post(config.url + "/message/send/v1", json={'token': token, 'channel_id': channel_id, 'message': MESSAGE})
    message_id = send_http.json()['message_id']

    # Create another channel
    channel1_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': 'channel', 'is_public': CHANNEL_IS_PUBLIC})
    channel1_id = channel1_http.json()['channel_id']

    # Share message to new channel
    share_http = requests.post(config.url + "/message/share/v1", json={'token': token, 'og_message_id': message_id, 'message': 'additional message', 'channel_id': channel1_id, 'dm_id': -1})
    assert share_http.status_code == SUCCESS
    
    # Check new channel's messages
    messages_http = requests.get(config.url + "/channel/messages/v2", params={'token': token, 'channel_id': channel1_id, 'start': 0})
    messages = messages_http.json()['messages']
    assert (messages[0]['message'] == 'Hello World! | additional message')



def test_message_share_dm_to_dm(register_user1, register_user2):
    user_req = register_user1
    token = user_req.json()['token']

    user1_req = register_user2
    u_id2 = user1_req.json()['auth_user_id']
    assert user1_req.status_code == SUCCESS

    # Create first DM
    dm_http = requests.post(config.url + "/dm/create/v1", json={'token': token, 'u_ids': [u_id2]})
    dm_id = dm_http.json()['dm_id']
    assert dm_http.status_code == SUCCESS

    # User 1 sends a Message in created DM
    send_http = requests.post(config.url + "/message/senddm/v1", json={'token': token, 'dm_id': dm_id, 'message': MESSAGE})
    message_id = send_http.json()['message_id']
    assert send_http.status_code == SUCCESS

    # Create second DM
    dm1_http = requests.post(config.url + "/dm/create/v1", json={'token': token, 'u_ids': [u_id2]})
    dm1_id = dm1_http.json()['dm_id']
    assert dm1_http.status_code == SUCCESS

    # Share message to new DM
    share_http = requests.post(config.url + "/message/share/v1", json={'token': token, 'og_message_id': message_id, 'message': 'additional message', 'channel_id': -1, 'dm_id': dm1_id})
    assert share_http.status_code == SUCCESS

    # Check new message is in new dm
    messages_http = requests.get(config.url + "/dm/messages/v1", params={'token': token, 'dm_id': dm1_id, 'start': 0})
    messages = messages_http.json()['messages']
    assert (messages[0]['message'] == 'Hello World! | additional message')

def test_share_channel_to_dm(register_user1, register_user2):
    user_req = register_user1
    token = user_req.json()['token']

    user1_req = register_user2
    token1 = user1_req.json()['token']
    u_id2 = user1_req.json()['auth_user_id']
    assert user1_req.status_code == SUCCESS

    # User 1 creates channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # User 2 joins the new channel
    join_req = requests.post(config.url + "/channel/join/v2", json={'token': token1, 'channel_id': channel_id})
    assert join_req.status_code == SUCCESS

    # User 1 sends a Message in created channel
    send_http = requests.post(config.url + "/message/send/v1", json={'token': token, 'channel_id': channel_id, 'message': MESSAGE})
    message_id = send_http.json()['message_id']

    # Create DM
    dm_http = requests.post(config.url + "/dm/create/v1", json={'token': token, 'u_ids': [u_id2]})
    dm_id = dm_http.json()['dm_id']
    assert dm_http.status_code == SUCCESS

    # Share message to new DM
    share_http = requests.post(config.url + "/message/share/v1", json={'token': token, 'og_message_id': message_id, 'message': 'additional message', 'channel_id': -1, 'dm_id': dm_id})
    assert share_http.status_code == SUCCESS

    # Check new message is in new dm
    messages_http = requests.get(config.url + "/dm/messages/v1", params={'token': token, 'dm_id': dm_id, 'start': 0})
    messages = messages_http.json()['messages']
    assert (messages[0]['message'] == 'Hello World! | additional message')



def test_share_dm_to_channel(register_user1, register_user2):
    user_req = register_user1
    token = user_req.json()['token']

    user1_req = register_user2
    u_id2 = user1_req.json()['auth_user_id']

    # Create DM
    dm_http = requests.post(config.url + "/dm/create/v1", json={'token': token, 'u_ids': [u_id2]})
    dm_id = dm_http.json()['dm_id']
    assert dm_http.status_code == SUCCESS

    # User 1 sends a Message in created DM
    send_http = requests.post(config.url + "/message/senddm/v1", json={'token': token, 'dm_id': dm_id, 'message': MESSAGE})
    message_id = send_http.json()['message_id']
    assert send_http.status_code == SUCCESS

    # User 1 creates channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # Share message to new channel
    share_http = requests.post(config.url + "/message/share/v1", json={'token': token, 'og_message_id': message_id, 'message': 'additional message', 'channel_id': channel_id, 'dm_id': -1})
    assert share_http.status_code == SUCCESS

    # Check new channel's messages
    messages_http = requests.get(config.url + "/channel/messages/v2", params={'token': token, 'channel_id': channel_id, 'start': 0})
    messages = messages_http.json()['messages']
    assert (messages[0]['message'] == 'Hello World! | additional message')


    


# Both ID's are invalid
def test_share_invalid_channel_dm_ids_input_error(register_user1):
    user_req = register_user1
    token = user_req.json()['token']

    # Create channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # Send a Message in created channel
    send_http = requests.post(config.url + "/message/send/v1", json={'token': token, 'channel_id': channel_id, 'message': MESSAGE})
    message_id = send_http.json()['message_id']

    # Put in Invalid channel IDS and dm ID
    share_http = requests.post(config.url + "/message/share/v1", json={'token': token, 'og_message_id': message_id, 'message': 'additional message', 'channel_id': 999, 'dm_id': 999})
    assert share_http.status_code == INPUT_ERROR


# No -1's
def test_share_no_negatives_input_error(register_user1, register_user2):
    user_req = register_user1
    token = user_req.json()['token']

    user1_req = register_user2
    u_id2 = user1_req.json()['auth_user_id']

    # Create channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # Send a Message in created channel
    send_http = requests.post(config.url + "/message/send/v1", json={'token': token, 'channel_id': channel_id, 'message': MESSAGE})
    message_id = send_http.json()['message_id']

    # Create first DM
    dm_http = requests.post(config.url + "/dm/create/v1", json={'token': token, 'u_ids': [u_id2]})
    dm_id = dm_http.json()['dm_id']
    assert dm_http.status_code == SUCCESS

    share_http = requests.post(config.url + "/message/share/v1", json={'token': token, 'og_message_id': message_id, 'message': 'additional message', 'channel_id': channel_id, 'dm_id': dm_id})
    assert share_http.status_code == INPUT_ERROR

# User must be part of channel/dm of OG Message
def test_share_invalid_msg_id_input_error(register_user1, register_user2):
    user_req = register_user1
    token = user_req.json()['token']

    user1_req = register_user2
    token1 = user1_req.json()['token']
    
    # User 1 creates channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # User 1 sends a Message in created channel
    send_http = requests.post(config.url + "/message/send/v1", json={'token': token, 'channel_id': channel_id, 'message': MESSAGE})
    message_id = send_http.json()['message_id']

    # User 1 makes another channel
    channel1_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': 'deez', 'is_public': CHANNEL_IS_PUBLIC})
    channel1_id = channel1_http.json()['channel_id']

    # User 2 joins the new channel
    join_req = requests.post(config.url + "/channel/join/v2", json={'token': token1, 'channel_id': channel1_id})
    assert join_req.status_code == SUCCESS

    # User 2 tries to share message from channel hes not in
    share_http = requests.post(config.url + "/message/share/v1", json={'token': token1, 'og_message_id': message_id, 'message': 'additional message', 'channel_id': channel1_id, 'dm_id': -1})
    assert share_http.status_code == INPUT_ERROR


# Message over 1000 Characters
def test_share_message_length_input_error(register_user1):
    user_req = register_user1
    token = user_req.json()['token']

    # Create channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # Send a Message in created channel
    send_http = requests.post(config.url + "/message/send/v1", json={'token': token, 'channel_id': channel_id, 'message': MESSAGE})
    message_id = send_http.json()['message_id']

    # Create another channel
    channel1_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': 'channel1', 'is_public': CHANNEL_IS_PUBLIC})
    channel1_id = channel1_http.json()['channel_id']

    # Share message to new channel
    share_http = requests.post(config.url + "/message/share/v1", json={'token': token, 'og_message_id': message_id, 'message': LONG_MESSAGE, 'channel_id': channel1_id, 'dm_id': -1})
    assert share_http.status_code == INPUT_ERROR
    

# Both ID's are valid, but user is not in the ch/dm they are trying to share to
def test_share_access_error(register_user1, register_user2):
    user_req = register_user1
    token = user_req.json()['token']

    user1_req = register_user2
    token1 = user1_req.json()['token']

    # User 1 creates channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # User 2 joins the new channel
    join_req = requests.post(config.url + "/channel/join/v2", json={'token': token1, 'channel_id': channel_id})
    assert join_req.status_code == SUCCESS

    # User 1 sends a Message in created channel
    send_http = requests.post(config.url + "/message/send/v1", json={'token': token, 'channel_id': channel_id, 'message': MESSAGE})
    message_id = send_http.json()['message_id']

    # User 1 makes another channel
    channel1_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': 'deez', 'is_public': CHANNEL_IS_PUBLIC})
    channel1_id = channel1_http.json()['channel_id']

    # User 2 tries to share message to channel hes not in
    share_http = requests.post(config.url + "/message/share/v1", json={'token': token1, 'og_message_id': message_id, 'message': LONG_MESSAGE, 'channel_id': channel1_id, 'dm_id': -1})
    assert share_http.status_code == ACCESS_ERROR
