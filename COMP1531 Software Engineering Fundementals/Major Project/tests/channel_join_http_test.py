import json
import requests
import pytest
from src import config

INPUT_ERROR = 400
ACCESS_ERROR = 403
SUCCESS = 200

@pytest.fixture(autouse=True)
def clear():
    requests.delete(config.url + "clear/v1")

########## CHANNEL_JOIN_V2 TESTS ##########

# Test for InputError when channel_id does not refer to a valid channel
def test_channel_join_v2_InputError_invalid_channel():
    # Create user
    user = requests.post(config.url + "auth/register/v2", json={
        'email': 'email@gmail.com',
        'password': 'password',
        'name_first': 'first',
        'name_last': 'last',
    })
    token = user.json()['token']

    # User tries to join invalid channel
    join_request = requests.post(config.url + "channel/join/v2", json={'token': token, 'channel_id': 123})
    assert join_request.status_code == INPUT_ERROR

# Test for InputError when authorised user is already a member of the channel
def test_channel_join_v2_InputError_existing_member():
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

    # User tries to join channel that they created
    join_request = requests.post(config.url + "channel/join/v2", json={'token': token, 'channel_id': channel_id})
    assert join_request.status_code == INPUT_ERROR

# Test for AccessError when channel_id refers to a private channel and token does not refer to a member 
# that is not already a channel member and is not a global owner
def test_channel_join_v2_AccessError():
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

    # Create private channel with user1
    channel = requests.post(config.url + "channels/create/v2", json={
        'token': token1, 
        'name': 'user1_channel', 
        'is_public': False,
    })
    channel_id = channel.json()['channel_id']

    # User2 tries to join private channel
    join_request = requests.post(config.url + "channel/join/v2", json={'token': token2, 'channel_id': channel_id})
    assert join_request.status_code == ACCESS_ERROR

# Test for AccessErorr when an invalid token is given
def test_channel_join_v2_AccessError_invalid_token():
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

    # Invalid token tries to join channel
    join_request = requests.post(config.url + "channel/join/v2", json={'token': "invalid", 'channel_id': channel_id})
    assert join_request.status_code == ACCESS_ERROR

# Test for normal functioning
def test_channel_join_v2_functioning():
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
    join_request = requests.post(config.url + "channel/join/v2", json={'token': token2, 'channel_id': channel_id})
    assert join_request.status_code == SUCCESS
    assert join_request.json() == {}

    # Check if new channel joined is in user2 list of channels
    channel_list_request = requests.get(config.url + "channels/list/v2", params={'token': token2})
    assert channel_list_request.status_code == SUCCESS
    assert channel_list_request.json() == {'channels' : [{
        'channel_id': channel_id,
        'name': 'user1_channel',
    }]
    }

# Test for when global owner tries to join a private channel
def test_global_join_private():
    # Create user1 (global owner)
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

    # Create private channel with user2
    channel = requests.post(config.url + "channels/create/v2", json={
        'token': token2, 
        'name': 'user2_channel', 
        'is_public': False,
    })
    channel_id = channel.json()['channel_id']
    
    # User1 joins channel
    join_request = requests.post(config.url + "channel/join/v2", json={'token': token1, 'channel_id': channel_id})
    assert join_request.status_code == SUCCESS
    assert join_request.json() == {}
    
    # Check if new channel joined is in user1 list of channels
    channel_list_request = requests.get(config.url + "channels/list/v2", params={'token': token1})
    assert channel_list_request.status_code == SUCCESS
    assert channel_list_request.json() == {'channels' : [{
        'channel_id': channel_id,
        'name': 'user2_channel',
    }]
    }
