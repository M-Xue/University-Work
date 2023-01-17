import json
import requests
import pytest
from src import config

INPUT_ERROR = 400
ACCESS_ERROR = 403
SUCCESS = 200

@pytest.fixture(autouse=True)
def clear():
    requests.delete(config.url + "/clear/v1")

########## CHANNEL_LEAVE_V1 TESTS ##########

# Test for InputError when an invalid channel is given
def test_channel_leave_InputError_invalid_channel():
    # Create user
    user = requests.post(config.url + "auth/register/v2", json={
        'email': 'email@gmail.com',
        'password': 'password',
        'name_first': 'first',
        'name_last': 'last',
    })
    token = user.json()['token']

    # User tries to leave invalid channel
    leave_request = requests.post(config.url + "channel/leave/v1", json={'token': token, 'channel_id': 123})
    assert leave_request.status_code == INPUT_ERROR

# Test for AccessErorr when an invalid token is given
def test_channel_leave_AccessError_invalid_token():
    # Create user
    user = requests.post(config.url + "auth/register/v2", json={
        'email': 'email@gmail.com',
        'password': 'password',
        'name_first': 'first',
        'name_last': 'last',
    })
    token = user.json()['token']

    # Create channel with user
    channel = requests.post(config.url + "channels/create/v2", json={
        'token': token, 
        'name': 'user_channel', 
        'is_public': True,
    })
    channel_id = channel.json()['channel_id']

    # Invalid token tries to leave channel
    leave_request = requests.post(config.url + "channel/leave/v1", json={'token': "invalid", 'channel_id': channel_id})
    assert leave_request.status_code == ACCESS_ERROR 

# Test for when channel_id is valid and the token does not refer to a member of the channel
def test_channel_leave_AccessError():
    # Create user1
    user1 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email1@gmail.com',
        'password': 'password1',
        'name_first': 'first1',
        'name_last': 'last1',
    })
    token1 = user1.json()['token']

    # Create user2
    user2 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email2@gmail.com',
        'password': 'password2',
        'name_first': 'first2',
        'name_last': 'last2',
    })
    token2 = user2.json()['token']

    # Create channel with user1
    channel = requests.post(config.url + "channels/create/v2", json={
        'token': token1, 
        'name': 'user1_channel', 
        'is_public': True,
    })
    channel_id = channel.json()['channel_id']

    # User2 tries to leave channel they are not a member of
    leave_request = requests.post(config.url + "channel/leave/v1", json={'token': token2, 'channel_id': channel_id})
    assert leave_request.status_code == ACCESS_ERROR 

# Test for normal functioning
def test_channel_leave_functioning():
    # Create user1
    user1 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email1@gmail.com',
        'password': 'password1',
        'name_first': 'first1',
        'name_last': 'last1',
    })
    token1 = user1.json()['token']

    # Create user2
    user2 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email2@gmail.com',
        'password': 'password2',
        'name_first': 'first2',
        'name_last': 'last2',
    })
    token2 = user2.json()['token']

    # Create channel with user1
    channel = requests.post(config.url + "channels/create/v2", json={
        'token': token1, 
        'name': 'user1_channel', 
        'is_public': True,
    })
    channel_id = channel.json()['channel_id']

    # User2 joins channel
    requests.post(config.url + "channel/join/v2", json={'token': token2, 'channel_id': channel_id})

    # User1 leaves channel
    leave_request = requests.post(config.url + "channel/leave/v1", json={'token': token1, 'channel_id': channel_id})
    assert leave_request.status_code == SUCCESS
    assert leave_request.json() == {}
    
    # Check if channel is no longer in user1 list of channels
    channel_list_request = requests.get(config.url + "channels/list/v2", params={'token': token1})
    assert channel_list_request.status_code == SUCCESS
    assert channel_list_request.json() == {'channels' : []}

    # User2 leaves channel
    leave_request = requests.post(config.url + "channel/leave/v1", json={'token': token2, 'channel_id': channel_id})
    assert leave_request.status_code == SUCCESS
    assert leave_request.json() == {}
    
    # Check if channel is no longer in user1 list of channels
    channel_list_request = requests.get(config.url + "channels/list/v2", params={'token': token2})
    assert channel_list_request.status_code == SUCCESS
    assert channel_list_request.json() == {'channels' : []}

# Test for InputError when user is the starter of an active standup in the channel
def test_channel_leave_InputError_active_standup_starter():
    # Create user
    user = requests.post(config.url + "auth/register/v2", json={
        'email': 'email@gmail.com',
        'password': 'password',
        'name_first': 'first',
        'name_last': 'last',
    })
    token = user.json()['token']

    # Create channel with user
    channel = requests.post(config.url + "channels/create/v2", json={
        'token': token, 
        'name': 'user_channel', 
        'is_public': True,
    })
    channel_id = channel.json()['channel_id']

    # User creates standup in channel
    requests.post(config.url + "standup/start/v1", json={
        'token': token,
        'channel_id': channel_id,
        'length': 10,
    })
    check = requests.get(config.url + "standup/active/v1", params={
        'token': token,
        'channel_id': channel_id
    })
    assert check.json()['is_active'] == True

    # User tries to leave channel
    leave_request = requests.post(config.url + "channel/leave/v1", json={'token': token, 'channel_id': channel_id})
    assert leave_request.status_code == INPUT_ERROR
