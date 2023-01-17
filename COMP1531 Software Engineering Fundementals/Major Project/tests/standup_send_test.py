import json
import textwrap
import time
import requests
import pytest
from src import config
INPUT_ERROR = 400
ACCESS_ERROR = 403
SUCCESS = 200

@pytest.fixture(autouse=True)
def clear():
    requests.delete(config.url + "clear/v1")
########## STANDUP_SEND_V1 TESTS ##########

def test_standup_send_InputError_invalid_channnel_id():
    # Create user
    user = requests.post(config.url + "auth/register/v2", json={
        'email': 'email@gmail.com',
        'password': 'password',
        'name_first': 'first',
        'name_last': 'last',
    })
    token = user.json()['token']

    # User tries to send a standup message in invalid channel
    send_request = requests.post(config.url + "standup/send/v1", json={
        'token': token,
        'channel_id': 123,
        'message': 'hi',
    })
    assert send_request.status_code == INPUT_ERROR 

def test_standup_send_InputError_invalid_length():
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

    # User starts a standup
    requests.post(config.url + "standup/start/v1", json={
        'token': token,
        'channel_id': channel_id,
        'length': 2,
    })

    # User tries to send a standup message over 1000 character
    send_request = requests.post(config.url + "standup/send/v1", json={
        'token': token,
        'channel_id': channel_id,
        'message': 'hi'*1000,
    })
    assert send_request.status_code == INPUT_ERROR 

def test_standup_send_InputError_inactive_standup():
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

    # User tries to send a message in a channel with no active standup
    send_request = requests.post(config.url + "standup/send/v1", json={
        'token': token,
        'channel_id': channel_id,
        'message': 'hi',
    })
    assert send_request.status_code == INPUT_ERROR 

def test_standup_send_AccessError_invalid_token():
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

    # User starts a standup
    requests.post(config.url + "standup/start/v1", json={
        'token': token,
        'channel_id': channel_id,
        'length': 2,
    })

    # Invalid token tries to send message in standup
    send_request = requests.post(config.url + "standup/send/v1", json={
        'token': "invalid",
        'channel_id': channel_id,
        'message': 'hi',
    })
    assert send_request.status_code == ACCESS_ERROR 

def test_standup_start_AccessError_user_not_member():
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

    # User2 tries to send message in standup in channel they are not part of
    send_request = requests.post(config.url + "standup/send/v1", json={
        'token': token2,
        'channel_id': channel_id,
        'message': 'hi',
    })
    assert send_request.status_code == ACCESS_ERROR 

def test_standup_send_functioning():
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

    # User starts a standup
    requests.post(config.url + "standup/start/v1", json={
        'token': token,
        'channel_id': channel_id,
        'length': 2,
    })
    send_request = requests.post(config.url + "standup/send/v1", json={
        'token': token,
        'channel_id': channel_id,
        'message': 'hi',
    })
    assert send_request.status_code == SUCCESS
    assert send_request.json() == {}

    time.sleep(2.5)

    # Check message was successfully sent
    channel_messages = requests.get(config.url + "channel/messages/v2", params={
        'token': token,
        'channel_id': channel_id,
        'start': 0
    })
    assert channel_messages.json()['messages'][0]['message'] == 'firstlast: hi\n'
    assert channel_messages.json()['messages'][0]['u_id'] == user.json()['auth_user_id']
    
def test_standup_send_multiple_messages_functioning():
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
    requests.post(config.url + 'channel/join/v2', json={
        'token': token2,
        'channel_id': channel_id
    })

    # User1 starts standup
    requests.post(config.url + "standup/start/v1", json={
        'token': token1,
        'channel_id': channel_id,
        'length': 2,
    })

    # User1 sends message in standup
    send_request = requests.post(config.url + "standup/send/v1", json={
        'token': token1,
        'channel_id': channel_id,
        'message': 'hi',
    })
    assert send_request.status_code == SUCCESS
    assert send_request.json() == {}

    # User2 sends a message in standup
    send_request = requests.post(config.url + "standup/send/v1", json={
        'token': token2,
        'channel_id': channel_id,
        'message': 'hello',
    })
    assert send_request.status_code == SUCCESS
    assert send_request.json() == {}

    time.sleep(2.5)

    # Check messages were successfully sent
    channel_messages = requests.get(config.url + "channel/messages/v2", params={
        'token': token1,
        'channel_id': channel_id,
        'start': 0
    })
    assert channel_messages.json()['messages'][0]['message'] == "first1last1: hi\nfirst2last2: hello\n"
    assert channel_messages.json()['messages'][0]['u_id'] == user1.json()['auth_user_id']
