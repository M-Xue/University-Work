import json
import requests
import pytest
from src.other import clear_v1
from src import config

INPUT_ERROR = 400
ACCESS_ERROR = 403
SUCCESS = 200

@pytest.fixture(autouse=True)
def clear():
    requests.delete(config.url + "clear/v1")

# invalid PUBLIC channel when name is more than 20 characters
def test_invalid_public_channel_name():
    channels_response = requests.post(config.url + "auth/register/v2", json={
        'email': 'email@gmail.com',
        'password': 'password',
        'name_first': 'first',
        'name_last': 'last',
    })
    token = channels_response.json()['token']

    channels_create = requests.post(config.url + 'channels/create/v2', json={
        "token": token,
        "name": "thisnameislongerthan20characters",
        "is_public": True,
    })
    assert channels_create.status_code == INPUT_ERROR

# invalid PRIVATE channel when name is more than 20 characters
def test_invalid_private_channel_name():
    channels_response = requests.post(config.url + "auth/register/v2", json={
        'email': 'email@gmail.com',
        'password': 'password',
        'name_first': 'first',
        'name_last': 'last',
    })
    token = channels_response.json()['token']

    channels_create = requests.post(config.url + 'channels/create/v2', json={
        "token": token,
        "name": "thisnameislongerthan20characters",
        "is_public": False,
    })
    assert channels_create.status_code == INPUT_ERROR

# invalid PUBLIC channel when channel name is less than 1 character
def test_invalid_public_channel_name_less_1():
    channels_response = requests.post(config.url + "auth/register/v2", json={
        'email': 'email@gmail.com',
        'password': 'password',
        'name_first': 'first',
        'name_last': 'last',
    })
    token = channels_response.json()['token']

    channels_create = requests.post(config.url + 'channels/create/v2', json={
        "token": token,
        "name": "",
        "is_public": True,
    })
    assert channels_create.status_code == INPUT_ERROR


# invalid PRIVATE channel when channel name is less than 1 character
def test_invalid_private_channel_name_less_than_1():
    channels_response = requests.post(config.url + "auth/register/v2", json={
        'email': 'email@gmail.com',
        'password': 'password',
        'name_first': 'first',
        'name_last': 'last',
    })
    token = channels_response.json()['token']

    channels_create = requests.post(config.url + 'channels/create/v2', json={
        "token": token,
        "name": "",
        "is_public": False,
    })
    assert channels_create.status_code == INPUT_ERROR

# valid creating public channel
def test_create_channel_public_success():
    channels_response = requests.post(config.url + "auth/register/v2", json={
        'email': 'email@gmail.com',
        'password': 'password',
        'name_first': 'first',
        'name_last': 'last',
    })
    token = channels_response.json()['token']

    channels_create = requests.post(config.url + 'channels/create/v2', json={
        "token": token,
        'name': "channel_1",
        "is_public": True})

    assert channels_create.status_code == SUCCESS
    assert channels_create.json() == {
        'channel_id': 1
    }

# valid creating private channel
def test_create_channel_private_success():
    channels_response = requests.post(config.url + "auth/register/v2", json={
        'email': 'email@gmail.com',
        'password': 'password',
        'name_first': 'first',
        'name_last': 'last',
    })
    token = channels_response.json()['token']

    channels_create = requests.post(config.url + 'channels/create/v2', json={
        "token": token,
        'name': "channel_1",
        "is_public": False})
    assert channels_create.status_code == SUCCESS
    assert channels_create.json() == {
        'channel_id': 1,
        }

# given token is invalid - public
def test_token_invalid_public():
    token = "-1"
    channels_response = requests.post(config.url + 'channels/create/v2', json={
        "token": token,
        'name': "channel_1",
        "is_public": True})
    assert channels_response.status_code == ACCESS_ERROR

# given token is invalid - private
def test_token_invalid_private():
    token = "-1"
    channels_response = requests.post(config.url + 'channels/create/v2', json={
        "token": token,
        'name': "channel_1",
        "is_public": False})
    assert channels_response.status_code == ACCESS_ERROR