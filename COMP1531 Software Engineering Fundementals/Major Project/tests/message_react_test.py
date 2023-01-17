from email import message
import json
import requests
import pytest
from src.other import clear_v1
from src import config
from tests.test_msg_helper import *
from src.auth import *
from src.security_helper import *
from src.helper import *
CHANNEL_NAME = "Name"
CHANNEL_IS_PUBLIC = True
INPUT_ERROR = 400
ACCESS_ERROR = 403
SUCCESS = 200
@pytest.fixture(autouse=True)
def clear():
  requests.delete(config.url + "clear/v1")
########## MESSAGE_REACT_V1 TESTS ##########


"""
Given a message within a channel or DM the authorised user is part of, add a "react" to that particular message.
"""
# AccessError: token given is invalid
def test_message_react_invalid_token():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_1 = user_1.json()['token']

    channel = channel_create(user_token_1, "channel", True)
    message = send_message(user_token_1, channel['channel_id'], 'hello!')

    message_react_http = requests.post(config.url + "/message/react/v1", json={
        'token': -1,
        'message_id': message['message_id'],
        'react_id': 1,
        })
    assert(message_react_http.status_code == ACCESS_ERROR)

# InputError: message_id is not a valid message within a channel/DM that the authorised user has joined
def test_message_react_invalid_message_id():
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
    message_react_http = requests.post(config.url + "/message/react/v1", json={
        'token': user_token_1,
        'message_id': -1,
        'react_id': 1,
        })
    assert (message_react_http.status_code == INPUT_ERROR)

# InputError: react_id is not a valid react ID in dms
def test_message_react_invalid_react_id_dms():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_1 = user_1.json()['token']

    dm = dm_create(user_token_1, []).json()
    message = senddm(user_token_1, dm['dm_id'], 'hello!').json()

    message_react_http = requests.post(config.url + "/message/react/v1", json={
        'token': user_token_1,
        'message_id': message['message_id'],
        'react_id': -1,
        })
    assert(message_react_http.status_code == INPUT_ERROR)

# InputError: react_id is not a valid react ID in channel
def test_message_react_invalid_react_id_channels():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_1 = user_1.json()['token']

    channel = channel_create(user_token_1, "channel", True)
    message = send_message(user_token_1, channel['channel_id'], 'hello!')

    message_react_http = requests.post(config.url + "/message/react/v1", json={
        'token': user_token_1,
        'message_id': message['message_id'],
        'react_id': 2,
        })
    assert(message_react_http.status_code == INPUT_ERROR)

# InputError: the message already contains a react with ID react_id from the authorised user (channels)
def test_message_react_double_react_channels():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_1 = user_1.json()['token']

    channel_1 = channel_create(user_token_1, "channel", True)
    message = send_message(user_token_1, channel_1['channel_id'], 'hello')

    message_react_http = requests.post(config.url + "/message/react/v1", json={
        'token': user_token_1,
        'message_id': message['message_id'],
        'react_id': 1,
        })
    assert(message_react_http.status_code == SUCCESS)

    message_react_double = requests.post(config.url + "/message/react/v1", json={
        'token': user_token_1,
        'message_id': message['message_id'],
        'react_id': 1,
        })
    assert(message_react_double.status_code == INPUT_ERROR)

# InputError: the message already contains a react with ID react_id from the authorised user (dms)
def test_message_react_double_react_dms():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_1 = user_1.json()['token']

    dm = dm_create(user_token_1, []).json()
    message = senddm(user_token_1, dm['dm_id'], 'hello!').json()

    message_react_http = requests.post(config.url + "/message/react/v1", json={
        'token': user_token_1,
        'message_id': message['message_id'],
        'react_id': 1,
        })
    assert(message_react_http.status_code == SUCCESS)

    message_react_double = requests.post(config.url + "/message/react/v1", json={
        'token': user_token_1,
        'message_id': message['message_id'],
        'react_id': 1,
        })
    assert(message_react_double.status_code == INPUT_ERROR)


# Valid message in dm react
def test_message_react_valid_dms():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_1 = user_1.json()['token']

    user_2 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email2@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_2 = user_2.json()['token']
    user_id_2 = user_2.json()['auth_user_id']

    dm_1 = dm_create(user_token_1, [user_id_2]).json()
    message = senddm(user_token_2, dm_1['dm_id'], 'hello!').json()

    message_react_http = requests.post(config.url + "/message/react/v1", json={
        'token': user_token_2,
        'message_id': message['message_id'],
        'react_id': 1,
        })
    assert(message_react_http.status_code == SUCCESS)

# Valid message in channel react
def test_message_react_valid_channels():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_1 = user_1.json()['token']

    channel_1 = channel_create(user_token_1, "channel", True)
    message = send_message(user_token_1, channel_1['channel_id'], 'hello!')

    message_react_http = requests.post(config.url + "/message/react/v1", json={
        'token': user_token_1,
        'message_id': message['message_id'],
        'react_id': 1,
        })
    assert(message_react_http.status_code == SUCCESS)


# valid: two users react to 1 message in channel
def test_message_2_react_1_message_valid_channels():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_1 = user_1.json()['token']
    user_id_1 = user_1.json()['auth_user_id']

    user_2 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email2@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_2 = user_2.json()['token']
    user_id_2 = user_2.json()['auth_user_id']

    channel_1 = channel_create(user_token_1, "channel", True)
    channel_join(user_token_2, channel_1['channel_id'])
    message = send_message(user_token_1, channel_1['channel_id'], 'hello!')

    message_react_http = requests.post(config.url + "/message/react/v1", json={
        'token': user_token_1,
        'message_id': message['message_id'],
        'react_id': 1,
        })
    assert(message_react_http.status_code == SUCCESS)

    ch_message = channel_message(user_token_1, channel_1['channel_id'], 0).json()
    assert ch_message['messages'][0]['reacts'][0]['u_ids'] == [user_id_1]

    message_react_http = requests.post(config.url + "/message/react/v1", json={
        'token': user_token_2,
        'message_id': message['message_id'],
        'react_id': 1,
        })
    assert(message_react_http.status_code == SUCCESS)
    ch_message = channel_message(user_token_1, channel_1['channel_id'], 0).json()
    assert ch_message['messages'][0]['reacts'][0]['u_ids'] == [user_id_1, user_id_2]


