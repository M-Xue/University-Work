import json
import requests
import pytest
from src.other import clear_v1
from src import config
from src.auth import *
from src.channels import *
from src.message import *
from tests.test_msg_helper import *


INPUT_ERROR = 400
ACCESS_ERROR = 403
SUCCESS = 200
INVALID = -1
LEN_INVALID_MSG = 'too long whoops' * 2000
LEN_SHORT_MSG = ''

@pytest.fixture(autouse=True)
def clear():
    requests.delete(config.url + "clear/v1")


#tests for message/send/v1
def test_success():
    user = auth_register('email@gmail.com', 'password', 'firstname', 'lastname')
    channel = channel_create(user['token'], "my channel", True)
    msg = send_message(user['token'], channel['channel_id'], 'hi!')
    assert msg == {'message_id': msg['message_id']}
 
def test_invalid_token_passed():
    register_response = auth_register('email@gmail.com', 'password', 'firstname', 'lastname')
    channel_response = channel_create(register_response['token'], "hi", True)
    message_response = requests.post(f'{config.url}message/send/v1', json={
        'token': INVALID,
        'channel_id': channel_response['channel_id'],
        'message': "Access Error"
    })
    assert message_response.status_code == ACCESS_ERROR
    
def test_invalid_channel():
    register_response = auth_register('email@gmail.com', 'password', 'firstname', 'lastname')
    message_response = requests.post(f'{config.url}message/send/v1', json={
        'token': register_response['token'],
        'channel_id': INVALID,
        'message': "Access Error"
    })
    assert message_response.status_code == INPUT_ERROR

def test_unauthorised_member():
    register_response = auth_register('email@gmail.com', 'password1', 'firstname1', 'lastname1')
    register_response2 = auth_register('email2@gmail.com', 'password2', 'firstname2', 'lastname2')
    channel_response = channel_create(register_response['token'], "hi", True)
    message_response = requests.post(f'{config.url}message/send/v1', json={
        'token': register_response2['token'],
        'channel_id': channel_response['channel_id'],
        'message': "hello"
    })
    assert message_response.status_code == ACCESS_ERROR

#input error when msg too short
def test_too_short_msg():
    user = auth_register('email@gmail.com', 'password', 'firstname', 'lastname')
    channel_create(user['token'], "channel", True)
    response = requests.post(f'{config.url}message/send/v1', json={
        'token': user['token'],
        'channel_id': 1,
        'message': LEN_SHORT_MSG,
    })
    assert response.status_code == INPUT_ERROR

#input error when more than 1k char
def test_more_than_1000_msg():
    user = auth_register('email123@gmail.com', 'password', 'firstname', 'lastname')
    channel_create(user['token'], "channel", True)
    response = requests.post(f'{config.url}message/send/v1', json={
       "token": user['token'],
       'channel_id': 1,
       "message": LEN_INVALID_MSG,
    })
    assert response.status_code == INPUT_ERROR
