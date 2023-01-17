import json
import requests
import pytest
from src.other import clear_v1
from src import config
from src.auth import *
from src.channels import *
from src.message import *
from tests.test_msg_helper import *
import datetime

INPUT_ERROR = 400
ACCESS_ERROR = 403
SUCCESS = 200
INVALID = -1
LEN_INVALID_MSG = 'too long whoops' * 2000
LEN_SHORT_MSG = ''
CURR_TIME = int(datetime.datetime.now().timestamp())

@pytest.fixture(autouse=True)
def clear():
    requests.delete(config.url + "clear/v1")

# access error when invalid token is passed
def test_invalid_token():
    register_response = auth_register('email@gmail.com', 'password', 'firstname', 'lastname')
    channel_response = channel_create(register_response['token'], "hi", True)

    message_response = requests.post(f'{config.url}message/sendlater/v1', json={
        'token': INVALID,
        'channel_id': channel_response['channel_id'],
        'message': "message",
        'time_sent': CURR_TIME + 1
    })
    assert message_response.status_code == ACCESS_ERROR

# input error when channel id is invalid
def test_invalid_channel():
    register_response = auth_register('email@gmail.com', 'password', 'firstname', 'lastname')
    message_response = requests.post(f'{config.url}message/sendlater/v1', json={
        'token': register_response['token'],
        'channel_id': INVALID,
        'message': "access error",
        'time_sent': CURR_TIME + 1
    })
    assert message_response.status_code == INPUT_ERROR

# access error when member is not authorised
def test_unauthorised_member():
    register_response = auth_register('email@gmail.com', 'password1', 'firstname1', 'lastname1')
    register_response2 = auth_register('email2@gmail.com', 'password2', 'firstname2', 'lastname2')
    channel_response = channel_create(register_response['token'], "hi", True)
    message_response = requests.post(f'{config.url}message/sendlater/v1', json={
        'token': register_response2['token'],
        'channel_id': channel_response['channel_id'],
        'message': "hello",
        'time_sent': CURR_TIME + 1
    })
    assert message_response.status_code == ACCESS_ERROR

#input error when msg too short
def test_too_short_msg():
    user = auth_register('email@gmail.com', 'password', 'firstname', 'lastname')
    channel_create(user['token'], "channel", True)
    response = requests.post(f'{config.url}message/sendlater/v1', json={
        'token': user['token'],
        'channel_id': 1,
        'message': LEN_SHORT_MSG,
        'time_sent': CURR_TIME + 1
    })
    assert response.status_code == INPUT_ERROR

#input error when more than 1k char
def test_more_than_1000_msg():
    user = auth_register('email123@gmail.com', 'password', 'firstname', 'lastname')
    channel_create(user['token'], "channel", True)
    response = requests.post(f'{config.url}message/sendlater/v1', json={
       "token": user['token'],
       'channel_id': 1,
       "message": LEN_INVALID_MSG,
       'time_sent': CURR_TIME + 1
    })
    assert response.status_code == INPUT_ERROR

# input error when message is sent in past
def test_msg_sent_in_past():
    user = auth_register('email123@gmail.com', 'password', 'firstname', 'lastname')
    channel_create(user['token'], "channel", True)
    response = requests.post(f'{config.url}message/sendlater/v1', json={
       "token": user['token'],
       'channel_id': 1,
       "message": 'message',
       'time_sent': CURR_TIME - 1
    })
    assert response.status_code == INPUT_ERROR




