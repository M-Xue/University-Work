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

########## CHANNEL_INVITE_V2 TESTS ##########

# Test for InputError when channel_id does not refer to a valid channel
def test_channel_invite_v1_InputError_invalid_channel():
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
    u_id2 = user2.json()['auth_user_id']

    # User1 tries to invite user2 to invalid channel
    invite_request = requests.post(config.url + "channel/invite/v2", json={
        'token': token1,
        'channel_id': 123,
        'u_id': u_id2,
    })
    assert invite_request.status_code == INPUT_ERROR

# Test for InputError when u_id does not refer to a valid user
def test_channel_invite_v1_InputError_invalid_user():
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

    # User tries to invite an invalid u_id to channel
    invite_request = requests.post(config.url + "channel/invite/v2", json={
        'token': token,
        'channel_id': channel_id,
        'u_id': 123,
    })
    assert invite_request.status_code == INPUT_ERROR

# Test for InputError when u_id is already a member of the channel
def test_channel_invite_v1_InputError_existing_member():
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
    u_id2 = user2.json()['auth_user_id']

    # Create channel with user1
    channel = requests.post(config.url + "channels/create/v2", json={
        'token': token1, 
        'name': 'user1_channel', 
        'is_public': True,
    })
    channel_id = channel.json()['channel_id']

    # User2 joins channel
    requests.post(config.url + "channel/join/v2", json={'token': token2, 'channel_id': channel_id})

    # User1 tries to invite user2 to channel they are already a member of 
    invite_request = requests.post(config.url + "channel/invite/v2", json={
        'token': token1,
        'channel_id': channel_id,
        'u_id': u_id2,
    })
    assert invite_request.status_code == INPUT_ERROR

# Test for AccessError when channel_id refers to a private channel and token does not refer to a member 
# that is not already a channel member and is not a global owner
def test_channel_invite_v1_AccessError():
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

    # Create user3
    user3 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email3@gmail.com',
        'password': 'password3',
        'name_first': 'first3',
        'name_last': 'last3',
    })
    u_id3 = user3.json()['auth_user_id']

    # Create channel with user1
    channel = requests.post(config.url + "channels/create/v2", json={
        'token': token1, 
        'name': 'user1_channel', 
        'is_public': True,
    })
    channel_id = channel.json()['channel_id']

    # User2 tries to invite user3 to channel they are not a member of
    invite_request = requests.post(config.url + "channel/invite/v2", json={
        'token': token2,
        'channel_id': channel_id,
        'u_id': u_id3,
    })
    assert invite_request.status_code == ACCESS_ERROR

# Test for AccessError when an invalid token
def test_channel_invite_v1_AccessError_invalid_token():
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
    u_id2 = user2.json()['auth_user_id']

    # Create channel with user1
    channel = requests.post(config.url + "channels/create/v2", json={
        'token': token1, 
        'name': 'user1_channel', 
        'is_public': True,
    })
    channel_id = channel.json()['channel_id']

    # Invalid token tries to invite user 2 to channel
    invite_request = requests.post(config.url + "channel/invite/v2", json={
        'token': "invalid",
        'channel_id': channel_id,
        'u_id': u_id2,
    })
    assert invite_request.status_code == ACCESS_ERROR

# Test for normal functioning
def test_channel_invite_v1_functioning():
    # Create user1
    user1 = requests.post(config.url + "auth/register/v2", json={
        "email": "email1@gmail.com",
        "password": "password1",
        "name_first": "first1",
        "name_last": "last1",
    })
    token1 = user1.json()['token']

    # Create user2
    user2 = requests.post(config.url + "auth/register/v2", json={
        "email": "email2@gmail.com",
        "password": "password2",
        "name_first": "first2",
        "name_last": "last2",
    })
    token2 = user2.json()['token']
    u_id2 = user2.json()['auth_user_id']

    # Create channel with user1
    channel = requests.post(config.url + "channels/create/v2", json={
        "token": token1, 
        "name": "user1_channel", 
        "is_public": True,
    })
    channel_id = channel.json()['channel_id']

    # User1 invites user2 to channel
    invite_request = requests.post(config.url + "channel/invite/v2", json={
        "token": token1,
        "channel_id": channel_id,
        "u_id": u_id2,
    })
    assert invite_request.status_code == SUCCESS

    # Check if new channel is in user2 list of channels
    channel_list_request = requests.get(config.url + "channels/list/v2", params={'token': token2})
    assert channel_list_request.json() == {'channels' : [{
        'channel_id': channel_id,
        'name': 'user1_channel',
    }]
    }
