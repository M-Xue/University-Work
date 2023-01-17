from email import message
import json
import requests
import pytest
from src.other import clear_v1
from src import config
from src.channel import channel_invite_v1
from tests.test_msg_helper import *
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
########## MESSAGE_PIN_V1 TESTS ##########

"""
Given a message within a channel or DM, mark it as "pinned".
"""
# InputError: token given is invalid
def test_message_pin_invalid_token():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_1 = user_1.json()['token']

    dm = dm_create(user_token_1, []).json()
    message = senddm(user_token_1, dm['dm_id'], 'hello!').json()

    message_pin_http = requests.post(config.url + "/message/pin/v1", json={
        'token': -1,
        'message_id': message['message_id'],
        })
    assert(message_pin_http.status_code == ACCESS_ERROR)


# InputError: message_id is not a valid message within a channel or DM that the authorised user has joined
def test_message_pin_invalid_message_id():
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
    message_pin_http = requests.post(config.url + "/message/pin/v1", json={
        'token': user_token_1,
        'message_id': -1,
        })
    assert(message_pin_http.status_code == INPUT_ERROR)

# InputError: user is not a member of channel
def test_message_pin_not_a_member_channel():
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

    channel = channel_create(user_token_1, "channel", True)
    message = send_message(user_token_1, channel['channel_id'], 'hello!')

    message_pin_http = requests.post(config.url + "/message/pin/v1", json={
        'token': user_token_2,
        'message_id': message['message_id'],
        })
    assert(message_pin_http.status_code == INPUT_ERROR)

# InputError: user is not a member of dms
def test_message_pin_not_a_member_dms():
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

    dm = dm_create(user_token_1, []).json()
    message = senddm(user_token_1, dm['dm_id'], 'hello!').json()

    message_pin_http = requests.post(config.url + "/message/pin/v1", json={
        'token': user_token_2,
        'message_id': message['message_id'],
        })
    assert(message_pin_http.status_code == INPUT_ERROR)

# InputError: the message is already pinned in a channel
def test_message_pin_invalid_already_pinned_channel():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_1 = user_1.json()['token']

    channel = channel_create(user_token_1, "channel", True)
    message = send_message(user_token_1, channel['channel_id'], 'hello!')

    message_pin_http = requests.post(config.url + "/message/pin/v1", json={
        'token': user_token_1,
        'message_id': message['message_id'],
        })
    assert(message_pin_http.status_code == SUCCESS)

    message_pin_double = requests.post(config.url + "/message/pin/v1", json={
        'token': user_token_1,
        'message_id': message['message_id'],
        })
    assert(message_pin_double.status_code == INPUT_ERROR)

# InputError: the message is already pinned in a dm
def test_message_pin_invalid_already_pinned_dm():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_1 = user_1.json()['token']

    dm = dm_create(user_token_1, []).json()
    message = senddm(user_token_1, dm['dm_id'], 'hello!').json()

    message_pin_http = requests.post(config.url + "/message/pin/v1", json={
        'token': user_token_1,
        'message_id': message['message_id'],
        })
    assert(message_pin_http.status_code == SUCCESS)

    message_pin_double = requests.post(config.url + "/message/pin/v1", json={
        'token': user_token_1,
        'message_id': message['message_id'],
        })
    assert(message_pin_double.status_code == INPUT_ERROR)


# AccessError: message_id refers to a valid message in a joined channel and the authorised user does not have owner permissions in the channel
def test_message_pin_no_owner_permission_channel():
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

    channel = channel_create(user_token_1, "channel", True)
    channel_join(user_token_2, channel['channel_id'])
    message = send_message(user_token_1, channel['channel_id'], 'hello!')

    message_pin_http = requests.post(config.url + "/message/pin/v1", json={
        'token': user_token_2,
        'message_id': message['message_id'],
        })
    assert(message_pin_http.status_code == ACCESS_ERROR)

# Valid: message is pinned by global owner
def test_message_pin_global_owner_channel():
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

    channel = channel_create(user_token_2, "channel", True)
    channel_join(user_token_1, channel['channel_id'])
    message = send_message(user_token_1, channel['channel_id'], 'hello!')

    message_pin_http = requests.post(config.url + "/message/pin/v1", json={
        'token': user_token_1,
        'message_id': message['message_id'],
        })
    assert(message_pin_http.status_code == SUCCESS)

# AccessError: message_id refers to a valid message in a joined DM and the authorised user does not have owner permissions in the DM
def test_message_pin_no_owner_permissions_dm():
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
    user_id_2 = user_2.json()['auth_user_id']
    user_token_2 = user_2.json()['token']

    dm = dm_create(user_token_1, [user_id_2]).json()
    message = senddm(user_token_2, dm['dm_id'], 'hello!').json()

    message_pin_http = requests.post(config.url + "/message/pin/v1", json={
        'token': user_token_2,
        'message_id': message['message_id'],
        })
    assert(message_pin_http.status_code == ACCESS_ERROR)

# valid: message is pinned in a dm
def test_message_pin_valid_dm():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_1 = user_1.json()['token']

    dm = dm_create(user_token_1, []).json()
    message = senddm(user_token_1, dm['dm_id'], 'hello!').json()

    message_pin_http = requests.post(config.url + "/message/pin/v1", json={
        'token': user_token_1,
        'message_id': message['message_id'],
        })
    assert(message_pin_http.status_code == SUCCESS)



# valid: message is pinned in a channel
def test_message_pin_valid_channel():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token_1 = user_1.json()['token']

    channel = channel_create(user_token_1, "channel", True)
    message = send_message(user_token_1, channel['channel_id'], 'hello!')

    message_pin_http = requests.post(config.url + "/message/pin/v1", json={
        'token': user_token_1,
        'message_id': message['message_id'],
        })
    assert(message_pin_http.status_code == SUCCESS)
