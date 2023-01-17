from email import message
import json
import requests
import pytest
from src.other import clear_v1
from src import config
from src.channel import channel_invite_v1
from tests.test_msg_helper import *
CHANNEL_NAME = "Name"
CHANNEL_IS_PUBLIC = True
INPUT_ERROR = 400
ACCESS_ERROR = 403
SUCCESS = 200
@pytest.fixture(autouse=True)
def clear():
  requests.delete(config.url + "clear/v1")
########## MESSAGE_UNREACT_V1 TESTS ##########

"""
Given a message within a channel or DM the authorised user is part of, remove a "react" to that particular message.
"""
# AccessError: token given is invalid
def test_message_unreact_invalid_token():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_1 = user_1.json()['token']

    dm = dm_create(user_token_1, []).json()
    message = senddm(user_token_1, dm['dm_id'], 'hello!').json()

    message_unreact_http = requests.post(config.url + "/message/unreact/v1", json={
        'token': -1,
        'message_id': message['message_id'],
        'react_id': 1,
        })
    assert(message_unreact_http.status_code == ACCESS_ERROR)


# InputError: message_id is not a valid message within a channel/DM that the authorised user has joined
def test_message_unreact_invalid_message_id():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_1 = user_1.json()['token']

    # User 1 creates channel
    channel = channel_create(user_token_1, "channel", True)
    # send a message
    send_message(user_token_1, channel['channel_id'], 'hello!')

    # check invalid message_id
    message_unreact_http = requests.post(config.url + "/message/unreact/v1", json={
        'token': user_token_1,
        'message_id': -1,
        'react_id': 1,
        })
    assert(message_unreact_http.status_code == INPUT_ERROR)

# InputError: react_id is not a valid react ID in dms
def test_message_unreact_invalid_react_id_dms():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_1 = user_1.json()['token']

    dm = dm_create(user_token_1, []).json()
    message = senddm(user_token_1, dm['dm_id'], 'hello!').json()

    message_unreact_http = requests.post(config.url + "/message/unreact/v1", json={
        'token': user_token_1,
        'message_id': message['message_id'],
        'react_id': -1,
        })
    assert(message_unreact_http.status_code == INPUT_ERROR)

# InputError: react_id is not a valid react ID in channel
def test_message_unreact_invalid_react_id_channels():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })

    user_token_1 = user_1.json()['token']

    channel = channel_create(user_token_1, "channel", True)
    message = send_message(user_token_1, channel['channel_id'], 'hello!')

    message_react(user_token_1, message['message_id'], 1)
    message_unreact_http = requests.post(config.url + "/message/unreact/v1", json={
        'token': user_token_1,
        'message_id': message['message_id'],
        'react_id': -1,
        })
    assert(message_unreact_http.status_code == INPUT_ERROR)

# InputError: the message does not contain a react with ID react_id from the authorised user(channels)
def test_message_unreact_no_react_in_message_channels():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_1 = user_1.json()['token']

    channel = channel_create(user_token_1, "channel", True)
    message = send_message(user_token_1, channel['channel_id'], 'hello!')

    message_unreact_http = requests.post(config.url + "/message/unreact/v1", json={
        'token': user_token_1,
        'message_id': message['message_id'],
        'react_id': 1,
        })
    assert(message_unreact_http.status_code == INPUT_ERROR)

# InputError: the message does not contain a react with ID react_id from the authorised user(dms)
def test_message_unreact_no_react_in_message_dms():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_1 = user_1.json()['token']

    dm = dm_create(user_token_1, []).json()
    message = senddm(user_token_1, dm['dm_id'], 'hello!').json()

    message_unreact_http = requests.post(config.url + "/message/unreact/v1", json={
        'token': user_token_1,
        'message_id': message['message_id'],
        'react_id': 1,
        })
    assert(message_unreact_http.status_code == INPUT_ERROR)

# Valid message in dm unreact - unreacts both messages
def test_message_unreact_valid_dms():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_1 = user_1.json()['token']


    dm_1 = dm_create(user_token_1, []).json()
    message = senddm(user_token_1, dm_1['dm_id'], 'hello!').json()

    message_react(user_token_1, message['message_id'], 1)

    message_unreact_http = requests.post(config.url + "/message/unreact/v1", json={
        'token': user_token_1,
        'message_id': message['message_id'],
        'react_id': 1,
        })
    assert(message_unreact_http.status_code == SUCCESS)

# Valid: unreact message in channel
def test_message_unreact_valid_channels():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_1 = user_1.json()['token']

    channel_1 = channel_create(user_token_1, "Channel", True)
    message = send_message(user_token_1, channel_1['channel_id'], 'hello!')

    message_react(user_token_1, message['message_id'], 1)

    message_unreact_http = requests.post(config.url + "/message/unreact/v1", json={
        'token': user_token_1,
        'message_id': message['message_id'],
        'react_id': 1,
        })
    assert(message_unreact_http.status_code == SUCCESS)
