import pytest
import requests
from src import config

CHANNEL_NAME = "Name"
CHANNEL_IS_PUBLIC = True
MESSAGE = "Hello World!"
SUCCESS = 200
INPUT_ERROR = 400
ACCESS_ERROR = 403

@pytest.fixture(autouse=True)
def clear():
    requests.delete(config.url + "clear/v1")

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

def test_user_stats_create_channel(register_user1):
    user_req = register_user1
    assert(user_req.status_code == 200)
    token = user_req.json()['token']

    # Create channel
    msg_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    assert msg_http.status_code == SUCCESS

    # Check stats to see if channels_joined increased
    stats_http = requests.get(config.url + "/user/stats/v1", params={'token': token})
    assert stats_http.status_code == SUCCESS
    stats = stats_http.json()['user_stats']

    channels_joined = stats['channels_joined']
    # num_channels_joined: 0 -> num_channels_joined: 1
    assert len(channels_joined) == 2
    assert channels_joined[0]['num_channels_joined'] == 0
    assert channels_joined[1]['num_channels_joined'] == 1


def test_user_stats_send_message_in_channel(register_user1):
    user_req = register_user1
    assert(user_req.status_code == 200)
    token = user_req.json()['token']

    # Create channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # Send a Message in created channel
    requests.post(config.url + "/message/send/v1", json={'token': token, 'channel_id': channel_id, 'message': MESSAGE})

    stats_http = requests.get(config.url + "/user/stats/v1", params={'token': token})
    assert stats_http.status_code == SUCCESS
    stats = stats_http.json()['user_stats']

    messages_sent = stats['messages_sent']
    assert len(messages_sent) == 2
    assert messages_sent[0]['num_messages_sent'] == 0
    assert messages_sent[1]['num_messages_sent'] == 1

def test_stats_leave_channel(register_user1):
    user_req = register_user1
    assert(user_req.status_code == 200)
    token = user_req.json()['token']

    # Create channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # User 1 leaves channel
    leave_request = requests.post(config.url + "channel/leave/v1", json={'token': token, 'channel_id': channel_id})
    assert leave_request.status_code == SUCCESS

    stats_http = requests.get(config.url + "/user/stats/v1", params={'token': token})
    assert stats_http.status_code == SUCCESS
    stats = stats_http.json()['user_stats']

    channels_joined = stats['channels_joined']
    # num_channels_joined: 0 -> num_channels_joined: 1 -> num_channels_joined: 0
    assert len(channels_joined) == 3
    assert channels_joined[0]['num_channels_joined'] == 0
    assert channels_joined[1]['num_channels_joined'] == 1
    assert channels_joined[2]['num_channels_joined'] == 0

def test_stats_channel_invite(register_user1, register_user2):
    user_req = register_user1
    token = user_req.json()['token']

    user1_req = register_user2
    token2 = user1_req.json()['token']
    u_id2 = user1_req.json()['auth_user_id']

    # Create channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # User 1 invites user 2 to channel
    requests.post(config.url + "channel/invite/v2", json={'token': token, 'channel_id': channel_id,'u_id': u_id2,})

    # Check user 2's stats to see if channels joined increased
    stats_http = requests.get(config.url + "/user/stats/v1", params={'token': token2})
    assert stats_http.status_code == SUCCESS
    stats = stats_http.json()['user_stats']
    channels_joined = stats['channels_joined']

    assert len(channels_joined) == 2
    assert channels_joined[0]['num_channels_joined'] == 0
    assert channels_joined[1]['num_channels_joined'] == 1

def test_stats_channel_join(register_user1, register_user2):
    user_req = register_user1
    token = user_req.json()['token']

    user1_req = register_user2
    token2 = user1_req.json()['token']

    # Create channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # User2 joins channel
    requests.post(config.url + "channel/join/v2", json={'token': token2, 'channel_id': channel_id})

    # Check user 2's stats to see if channels joined increased
    stats_http = requests.get(config.url + "/user/stats/v1", params={'token': token2})
    assert stats_http.status_code == SUCCESS
    stats = stats_http.json()['user_stats']
    channels_joined = stats['channels_joined']

    assert len(channels_joined) == 2
    assert channels_joined[0]['num_channels_joined'] == 0
    assert channels_joined[1]['num_channels_joined'] == 1

def test_stats_create_dm(register_user1, register_user2):
    user_req = register_user1
    token = user_req.json()['token']

    user1_req = register_user2
    u_id2 = user1_req.json()['auth_user_id']

    # Create first DM
    dm_http = requests.post(config.url + "/dm/create/v1", json={'token': token, 'u_ids': [u_id2]})
    assert dm_http.status_code == SUCCESS
    
    stats_http = requests.get(config.url + "/user/stats/v1", params={'token': token})
    assert stats_http.status_code == SUCCESS
    stats = stats_http.json()['user_stats']
    dms_joined = stats['dms_joined']
    # num_dms_joined: 0 -> num_dums_joined: 1
    assert len(dms_joined) == 2
    assert dms_joined[0]['num_dms_joined'] == 0
    assert dms_joined[1]['num_dms_joined'] == 1


def test_stats_invited_dm(register_user1, register_user2):
    user_req = register_user1
    token = user_req.json()['token']

    user1_req = register_user2
    token1 = user1_req.json()['token']
    u_id2 = user1_req.json()['auth_user_id']

    # User1 creates first DM with user 2
    dm_http = requests.post(config.url + "/dm/create/v1", json={'token': token, 'u_ids': [u_id2]})
    assert dm_http.status_code == SUCCESS
    
    # Check user 2 stats to see if they joined a DM
    stats_http = requests.get(config.url + "/user/stats/v1", params={'token': token1})
    assert stats_http.status_code == SUCCESS
    stats = stats_http.json()['user_stats']
    dms_joined = stats['dms_joined']
    # num_dms_joined: 0 -> num_dums_joined: 1
    assert len(dms_joined) == 2
    assert dms_joined[0]['num_dms_joined'] == 0
    assert dms_joined[1]['num_dms_joined'] == 1
    

def test_stats_send_dm(register_user1, register_user2):
    user_req = register_user1
    token = user_req.json()['token']

    user1_req = register_user2
    u_id2 = user1_req.json()['auth_user_id']

    # Create first DM
    dm_http = requests.post(config.url + "/dm/create/v1", json={'token': token, 'u_ids': [u_id2]})
    dm_id = dm_http.json()['dm_id']
    assert dm_http.status_code == SUCCESS

    # User 1 sends a Message in created DM
    send_http = requests.post(config.url + "/message/senddm/v1", json={'token': token, 'dm_id': dm_id, 'message': MESSAGE})
    assert send_http.status_code == SUCCESS

    stats_http = requests.get(config.url + "/user/stats/v1", params={'token': token})
    assert stats_http.status_code == SUCCESS
    stats = stats_http.json()['user_stats']

    messages_sent = stats['messages_sent']
    assert len(messages_sent) == 2
    assert messages_sent[0]['num_messages_sent'] == 0
    assert messages_sent[1]['num_messages_sent'] == 1

def test_stats_dm_leave(register_user1, register_user2):
    user_req = register_user1
    token = user_req.json()['token']

    user1_req = register_user2
    u_id2 = user1_req.json()['auth_user_id']

    # Create first DM
    dm_http = requests.post(config.url + "/dm/create/v1", json={'token': token, 'u_ids': [u_id2]})
    dm_id = dm_http.json()['dm_id']
    assert dm_http.status_code == SUCCESS

    # User 1 leaves the DM
    dm_leave_http = requests.post(config.url + "dm/leave/v1", json={'token': token, 'dm_id': dm_id})
    assert dm_leave_http.status_code == SUCCESS

    stats_http = requests.get(config.url + "/user/stats/v1", params={'token': token})
    assert stats_http.status_code == SUCCESS
    stats = stats_http.json()['user_stats']

    dms_joined = stats['dms_joined']
    # num_dms_joined: 0 -> num_dums_joined: 1 -> num_dums_joined: 0
    assert len(dms_joined) == 3
    assert dms_joined[0]['num_dms_joined'] == 0
    assert dms_joined[1]['num_dms_joined'] == 1
    assert dms_joined[2]['num_dms_joined'] == 0


# Test for when denominator is 0, involvement = 0
def test_stats_involvement_zero(register_user1):
    user_req = register_user1
    token = user_req.json()['token']

    stats_http = requests.get(config.url + "/user/stats/v1", params={'token': token})
    assert stats_http.status_code == SUCCESS
    stats = stats_http.json()['user_stats']

    involvement_rate = stats['involvement_rate']
    assert involvement_rate == 0

# Test for when Inolvement rate is greater than 1, involvement = 1
def test_stats_involvement_greater_1(register_user1):
    user_req = register_user1
    assert(user_req.status_code == 200)
    token = user_req.json()['token']

    # Create channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # Send a Message in created channel
    msg_http = requests.post(config.url + "/message/send/v1", json={'token': token, 'channel_id': channel_id, 'message': MESSAGE})
    msg_id = msg_http.json()['message_id']

    # Remove sent message - Doesn't affect num_msgs_sent but len(messages) goes down
    remove_http = requests.delete(config.url + "/message/remove/v1", json={'token': token, 'message_id': msg_id})
    assert remove_http.status_code == SUCCESS

    stats_http = requests.get(config.url + "/user/stats/v1", params={'token': token})
    assert stats_http.status_code == SUCCESS
    stats = stats_http.json()['user_stats']

    involvement_rate = stats['involvement_rate']
    assert involvement_rate == 1
