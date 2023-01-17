import json
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
########## STANDUP_START_V1 TESTS ##########

def test_standup_start_InputError_invalid_channnel_id():
    # Create user
    user = requests.post(config.url + "auth/register/v2", json={
        'email': 'email@gmail.com',
        'password': 'password',
        'name_first': 'first',
        'name_last': 'last',
    })
    token = user.json()['token']

    # User tries to start a standup in invalid channel
    start_request = requests.post(config.url + "standup/start/v1", json={
        'token': token,
        'channel_id': 123,
        'length': 2,
    })
    assert start_request.status_code == INPUT_ERROR 

def test_standup_start_InputError_negative_length():
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

    # User tries to start a standup with invalid length
    start_request = requests.post(config.url + "standup/start/v1", json={
        'token': token,
        'channel_id': channel_id,
        'length': -1,
    })
    assert start_request.status_code == INPUT_ERROR 

def test_standup_start_InputError_already_active_standup():
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

    # User tries to starts another standup while the first one is runnning
    start_request = requests.post(config.url + "standup/start/v1", json={
        'token': token,
        'channel_id': channel_id,
        'length': 2,
    })
    assert start_request.status_code == INPUT_ERROR 


def test_standup_start_AccessError_invalid_token():
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

    # Invalid token tries to start standup
    start_request = requests.post(config.url + "standup/start/v1", json={
        'token': "invalid",
        'channel_id': channel_id,
        'length': 2,
    })
    assert start_request.status_code == ACCESS_ERROR 

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

    # User2 tries to start standup in channel they are not part of
    start_request = requests.post(config.url + "standup/start/v1", json={
        'token': token2,
        'channel_id': channel_id,
        'length': 2,
    })
    assert start_request.status_code == ACCESS_ERROR 

def test_standup_start_functioning():
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
    start_request = requests.post(config.url + "standup/start/v1", json={
        'token': token,
        'channel_id': channel_id,
        'length': 2,
    })
    assert start_request.status_code == SUCCESS
    assert start_request.json() == {'time_finish': start_request.json()['time_finish']}

    # Check if standup is active
    check_request = requests.get(config.url + "standup/active/v1", params={
        'token': token,
        'channel_id': channel_id,
    })
    assert check_request.status_code == SUCCESS
    assert check_request.json()['is_active'] == True

    time.sleep(3)

    # Check if standup ended
    check_request = requests.get(config.url + "standup/active/v1", params={
        'token': token,
        'channel_id': channel_id,
    })
    assert check_request.status_code == SUCCESS
    assert check_request.json()['is_active'] == False
