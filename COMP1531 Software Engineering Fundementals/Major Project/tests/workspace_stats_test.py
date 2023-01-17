from venv import create
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

def test_workspace_create_channel(register_user1):
    user_req = register_user1
    assert(user_req.status_code == 200)
    token = user_req.json()['token']

    # Create channel
    create_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    assert create_http.status_code == SUCCESS

    # Check stats to see if channels_joined increased
    stats_http = requests.get(config.url + "/users/stats/v1", params={'token': token})
    assert stats_http.status_code == SUCCESS
    stats = stats_http.json()['workspace_stats']
    channels_exist = stats['channels_exist']
    # num_channels_exist: 0 -> num_channels_exist: 1
    assert(len(channels_exist) == 2)
    assert(channels_exist[0]['num_channels_exist'] == 0)
    assert(channels_exist[1]['num_channels_exist'] == 1)

def test_workspace_send_msg_channel(register_user1):
    user_req = register_user1
    assert(user_req.status_code == 200)
    token = user_req.json()['token']

    # Create channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # Send a Message in created channel
    requests.post(config.url + "/message/send/v1", json={'token': token, 'channel_id': channel_id, 'message': MESSAGE})

    # Check stats to see if channels_joined increased
    stats_http = requests.get(config.url + "/users/stats/v1", params={'token': token})
    assert stats_http.status_code == SUCCESS
    stats = stats_http.json()['workspace_stats']

    messages_exist= stats['messages_exist']
    assert len(messages_exist) == 2
    assert messages_exist[0]['num_messages_exist'] == 0
    assert messages_exist[1]['num_messages_exist'] == 1

def test_workspace_remove_msg_in_channel(register_user1):
    user_req = register_user1
    assert(user_req.status_code == 200)
    token = user_req.json()['token']

    # Create channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # Send a Message in created channel
    message_http = requests.post(config.url + "/message/send/v1", json={'token': token, 'channel_id': channel_id, 'message': MESSAGE})
    message_id = message_http.json()['message_id']
    # Remove sent message
    remove_http = requests.delete(config.url + "/message/remove/v1", json={'token': token, 'message_id': message_id})
    assert remove_http.status_code == SUCCESS

    stats_http = requests.get(config.url + "/users/stats/v1", params={'token': token})
    assert stats_http.status_code == SUCCESS
    stats = stats_http.json()['workspace_stats']

    messages_exist = stats['messages_exist']
    assert len(messages_exist) == 3
    assert messages_exist[0]['num_messages_exist'] == 0
    assert messages_exist[1]['num_messages_exist'] == 1
    assert messages_exist[2]['num_messages_exist'] == 0


def test_workspace_create_dm(register_user1, register_user2):
    user_req = register_user1
    token = user_req.json()['token']

    user1_req = register_user2
    u_id2 = user1_req.json()['auth_user_id']

    # Create first DM
    dm_http = requests.post(config.url + "/dm/create/v1", json={'token': token, 'u_ids': [u_id2]})
    assert dm_http.status_code == SUCCESS

    stats_http = requests.get(config.url + "/users/stats/v1", params={'token': token})
    assert stats_http.status_code == SUCCESS
    stats = stats_http.json()['workspace_stats']

    dms_exist = stats['dms_exist']
    assert len(dms_exist) == 2
    assert dms_exist[0]['num_dms_exist'] == 0
    assert dms_exist[1]['num_dms_exist'] == 1


def test_workspace_remove_dm(register_user1, register_user2):
    user_req = register_user1
    token = user_req.json()['token']

    user1_req = register_user2
    u_id2 = user1_req.json()['auth_user_id']

    # Create first DM
    dm_http = requests.post(config.url + "/dm/create/v1", json={'token': token, 'u_ids': [u_id2]})
    assert dm_http.status_code == SUCCESS
    dm_id = dm_http.json()['dm_id']

    # Remove DM
    remove_http = requests.delete(config.url + "/dm/remove/v1", json={'token': token, 'dm_id': dm_id})
    assert remove_http.status_code == SUCCESS

    stats_http = requests.get(config.url + "/users/stats/v1", params={'token': token})
    assert stats_http.status_code == SUCCESS
    stats = stats_http.json()['workspace_stats']

    dms_exist = stats['dms_exist']
    assert len(dms_exist) == 3
    assert dms_exist[0]['num_dms_exist'] == 0
    assert dms_exist[1]['num_dms_exist'] == 1
    assert dms_exist[2]['num_dms_exist'] == 0

def test_workspace_join_channel_and_dm(register_user1, register_user2):
    user_req = register_user1
    token = user_req.json()['token']

    user1_req = register_user2
    u_id2 = user1_req.json()['auth_user_id']

    # Create channel
    requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})

    # Create first DM
    dm_http = requests.post(config.url + "/dm/create/v1", json={'token': token, 'u_ids': [u_id2]})
    assert dm_http.status_code == SUCCESS

    stats_http = requests.get(config.url + "/users/stats/v1", params={'token': token})
    assert stats_http.status_code == SUCCESS
    stats = stats_http.json()['workspace_stats']

    channels_exist = stats['channels_exist']
    assert(len(channels_exist) == 2)
    assert(channels_exist[0]['num_channels_exist'] == 0)
    assert(channels_exist[1]['num_channels_exist'] == 1)


    dms_exist = stats['dms_exist']
    assert len(dms_exist) == 2
    assert dms_exist[0]['num_dms_exist'] == 0
    assert dms_exist[1]['num_dms_exist'] == 1

    # User 1 and user 2 is in at least 1 channel/dm
    utilization_rate = stats['utilization_rate']
    assert utilization_rate == 1

def test_workspace_send_msg_dm(register_user1, register_user2):
    user_req = register_user1
    token = user_req.json()['token']

    user1_req = register_user2
    u_id2 = user1_req.json()['auth_user_id']

    # Create first DM
    dm_http = requests.post(config.url + "/dm/create/v1", json={'token': token, 'u_ids': [u_id2]})
    assert dm_http.status_code == SUCCESS
    dm_id = dm_http.json()['dm_id']

    # User 1 sends a Message in created DM
    send_http = requests.post(config.url + "/message/senddm/v1", json={'token': token, 'dm_id': dm_id, 'message': MESSAGE})
    assert send_http.status_code == SUCCESS

    stats_http = requests.get(config.url + "/users/stats/v1", params={'token': token})
    assert stats_http.status_code == SUCCESS
    stats = stats_http.json()['workspace_stats']

    messages_exist = stats['messages_exist']
    assert len(messages_exist) == 2
    assert messages_exist[0]['num_messages_exist'] == 0
    assert messages_exist[1]['num_messages_exist'] == 1


def test_workspace_remove_message_in_dm(register_user1, register_user2):
    user_req = register_user1
    token = user_req.json()['token']

    user1_req = register_user2
    u_id2 = user1_req.json()['auth_user_id']

    # Create first DM
    dm_http = requests.post(config.url + "/dm/create/v1", json={'token': token, 'u_ids': [u_id2]})
    assert dm_http.status_code == SUCCESS
    dm_id = dm_http.json()['dm_id']

    # User 1 sends a Message in created DM
    send_http = requests.post(config.url + "/message/senddm/v1", json={'token': token, 'dm_id': dm_id, 'message': MESSAGE})
    message_id = send_http.json()['message_id']
    assert send_http.status_code == SUCCESS

    # Remove sent message
    remove_http = requests.delete(config.url + "/message/remove/v1", json={'token': token, 'message_id': message_id})
    assert remove_http.status_code == SUCCESS

    stats_http = requests.get(config.url + "/users/stats/v1", params={'token': token})
    assert stats_http.status_code == SUCCESS
    stats = stats_http.json()['workspace_stats']

    messages_exist = stats['messages_exist']
    assert len(messages_exist) == 3
    assert messages_exist[0]['num_messages_exist'] == 0
    assert messages_exist[1]['num_messages_exist'] == 1
    assert messages_exist[2]['num_messages_exist'] == 0

def test_workspace_send_msg_dm_remove_dm(register_user1, register_user2):
    user_req = register_user1
    token = user_req.json()['token']

    user1_req = register_user2
    u_id2 = user1_req.json()['auth_user_id']

    # Create first DM
    dm_http = requests.post(config.url + "/dm/create/v1", json={'token': token, 'u_ids': [u_id2]})
    assert dm_http.status_code == SUCCESS
    dm_id = dm_http.json()['dm_id']

    # User 1 sends a Message in created DM
    send_http = requests.post(config.url + "/message/senddm/v1", json={'token': token, 'dm_id': dm_id, 'message': MESSAGE})
    assert send_http.status_code == SUCCESS

    # User 1 sends a Message in created DM
    send1_http = requests.post(config.url + "/message/senddm/v1", json={'token': token, 'dm_id': dm_id, 'message': MESSAGE})
    assert send1_http.status_code == SUCCESS

    # Remove DM
    remove_http = requests.delete(config.url + "/dm/remove/v1", json={'token': token, 'dm_id': dm_id})
    assert remove_http.status_code == SUCCESS
    
    stats_http = requests.get(config.url + "/users/stats/v1", params={'token': token})
    assert stats_http.status_code == SUCCESS
    stats = stats_http.json()['workspace_stats']

    dms_exist = stats['dms_exist']
    assert len(dms_exist) == 3
    assert dms_exist[0]['num_dms_exist'] == 0
    assert dms_exist[1]['num_dms_exist'] == 1
    assert dms_exist[2]['num_dms_exist'] == 0

    # Now check whether messages_exist also decreased
    messages_exist = stats['messages_exist']
    assert len(messages_exist) == 4
    assert messages_exist[0]['num_messages_exist'] == 0
    assert messages_exist[1]['num_messages_exist'] == 1
    assert messages_exist[2]['num_messages_exist'] == 2
    assert messages_exist[3]['num_messages_exist'] == 0


def test_workspace_remove_user(register_user1, register_user2):
    user_req = register_user1
    token = user_req.json()['token']

    user1_req = register_user2
    u_id2 = user1_req.json()['auth_user_id']
    token2 = user1_req.json()['token']

    # Let user 1 make channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': 'Name', 'is_public': True})
    channel_id = channel_http.json()['channel_id']

    # User 2 joins the channel
    join_req = requests.post(config.url + "/channel/join/v2", json={'token': token2, 'channel_id': channel_id})
    assert join_req.status_code == SUCCESS

    stats_http = requests.get(config.url + "/users/stats/v1", params={'token': token})
    assert stats_http.status_code == SUCCESS
    stats = stats_http.json()['workspace_stats']
    # 2/2 == 1
    utilization_rate = stats['utilization_rate']
    assert utilization_rate == 1

    # User 1 (seams owner) removes user 2 (member)
    remove_http = requests.delete(config.url + "/admin/user/remove/v1", json={'token': token, 'u_id': u_id2})
    assert remove_http.status_code == SUCCESS

    stats1_http = requests.get(config.url + "/users/stats/v1", params={'token': token})
    assert stats1_http.status_code == SUCCESS
    stats1 = stats1_http.json()['workspace_stats']

    # 1/1 == 1
    utilization_rate1 = stats1['utilization_rate']
    assert utilization_rate1 == 1

def test_workspace_utilzation_rate(register_user1, register_user2):
    user_req = register_user1
    token = user_req.json()['token']

    register_user2

    # Let user 1 make channel
    requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': 'Name', 'is_public': True})

    # User 2 is in no DMS/channels

    stats1_http = requests.get(config.url + "/users/stats/v1", params={'token': token})
    assert stats1_http.status_code == SUCCESS
    stats1 = stats1_http.json()['workspace_stats']

    # 1/2 == 0.5
    utilization_rate1 = stats1['utilization_rate']
    assert utilization_rate1 == 0.5

def test_workspace_utilzation_rate_zero(register_user1):
    user_req = register_user1
    token = user_req.json()['token']
    
    # There are no channels/dms
    stats1_http = requests.get(config.url + "/users/stats/v1", params={'token': token})
    assert stats1_http.status_code == SUCCESS
    stats1 = stats1_http.json()['workspace_stats']
    # 0 / 1 == 0
    utilization_rate1 = stats1['utilization_rate']
    assert utilization_rate1 == 0