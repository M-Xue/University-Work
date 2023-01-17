import pytest
import requests
from src import config

CHANNEL_NAME = "Name"
CHANNEL_IS_PUBLIC = True
MESSAGE = "Hello World!"
SUCCESS = 200
INPUT_ERROR = 400
ACCESS_ERROR = 403
NONEXISTANT_MSG_ID = 11111

@pytest.fixture(autouse=True)
def clear_data():
    requests.delete(config.url + "/clear/v1")

def test_remove_message_channel():
    # Create user 1
    token_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token = token_http.json()['token']
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']
    # Send a Message in created channel
    send_http = requests.post(config.url + "/message/send/v1", json={'token': token, 'channel_id': channel_id, 'message': MESSAGE})
    message_id = send_http.json()['message_id']
    assert send_http.status_code == SUCCESS
    # Remove sent message
    remove_http = requests.delete(config.url + "/message/remove/v1", json={'token': token, 'message_id': message_id})
    assert remove_http.status_code == SUCCESS
    # Check that messages is now empty
    messages_http = requests.get(config.url + "/channel/messages/v2", params={'token': token, 'channel_id': channel_id, 'start': 0})
    assert(messages_http.json()['messages'] == [])

def test_remove_message_DM():
    # Create user 1
    token1_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token1 = token1_http.json()['token']

    token2_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example2@gmail.com',
        'password': 'password',
        'name_first': 'Dave',
        'name_last': 'Cave'})
    u_id2 = token2_http.json()['auth_user_id']
    
    dm_http = requests.post(config.url + "/dm/create/v1", json={'token': token1, 'u_ids': [u_id2]})
    dm_id = dm_http.json()['dm_id']
    assert dm_http.status_code == SUCCESS

    # User 1 sends a Message in created DM
    send_http = requests.post(config.url + "/message/senddm/v1", json={'token': token1, 'dm_id': dm_id, 'message': MESSAGE})
    message_id = send_http.json()['message_id']
    assert send_http.status_code == SUCCESS

    # Remove sent message
    remove_http = requests.delete(config.url + "/message/remove/v1", json={'token': token1, 'message_id': message_id})
    assert remove_http.status_code == SUCCESS

    # Check that messages is now empty
    messages_http = requests.get(config.url + "/dm/messages/v1", params={'token': token1, 'dm_id': dm_id, 'start': 0})
    assert(messages_http.json()['messages'] == [])

# Testing Owner Permissions
def test_remove_others_message_when_seams_owner():
    # Create user 1
    token1_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token1 = token1_http.json()['token']
    # Create user 2
    token2_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example2@gmail.com',
        'password': 'password',
        'name_first': 'Dave',
        'name_last': 'Cave'})
    token2 = token2_http.json()['token']
    # User 2 Creates channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token2, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # User 1 joins the channel
    join_req = requests.post(config.url + "/channel/join/v2", json={'token': token1, 'channel_id': channel_id})
    assert join_req.status_code == SUCCESS

    # User 2 sends a message
    send_http = requests.post(config.url + "/message/send/v1", json={'token': token2, 'channel_id': channel_id, 'message': MESSAGE})
    message_id = send_http.json()['message_id']

    # User 1 (SEAMS owner) removes user 2's message
    remove_http = requests.delete(config.url + "/message/remove/v1", json={'token': token1, 'message_id': message_id})
    assert remove_http.status_code == SUCCESS

    # Check Messages to see if it is empty
    messages_http = requests.get(config.url + "/channel/messages/v2", params={'token': token2, 'channel_id': channel_id, 'start': 0})
    assert(messages_http.json()['messages'] == [])

def test_remove_when_channel_owner():
    token1_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token1 = token1_http.json()['token']

    token2_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example2@gmail.com',
        'password': 'password',
        'name_first': 'Dave',
        'name_last': 'Cave'})
    token2 = token2_http.json()['token']

    # User 2 creates the channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token2, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # User 1 joins the channel
    join_req = requests.post(config.url + "/channel/join/v2", json={'token': token1, 'channel_id': channel_id})
    assert join_req.status_code == SUCCESS

    # User 1 sends a message into channel
    send_http = requests.post(config.url + "/message/send/v1", json={'token': token1, 'channel_id': channel_id, 'message': MESSAGE})
    message_id = send_http.json()['message_id']
    assert send_http.status_code == SUCCESS
    # User 2 (channel owner) removes user 1's message
    remove_http = requests.delete(config.url + "/message/remove/v1", json={'token': token2, 'message_id': message_id})
    assert remove_http.status_code == SUCCESS

    # Check to see if messages are empty
    messages_http = requests.get(config.url + "/channel/messages/v2", params={'token': token2, 'channel_id': channel_id, 'start': 0})
    assert(messages_http.json()['messages'] == [])

def test_remove_input_error():
    token_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token = token_http.json()['token']
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']
    # Send a Message in created channel
    requests.post(config.url + "/message/send/v1", json={'token': token, 'channel_id': channel_id, 'message': MESSAGE})
    # Try to remove message with MSG_ID that doesn't exist
    remove_http = requests.delete(config.url + "/message/remove/v1", json={'token': token, 'message_id': NONEXISTANT_MSG_ID})
    assert(remove_http.status_code == INPUT_ERROR)

def test_remove_access_error_not_in_channel():
    token1_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token1 = token1_http.json()['token']

    token2_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example2@gmail.com',
        'password': 'password',
        'name_first': 'Dave',
        'name_last': 'Cave'})
    token2 = token2_http.json()['token']
    # User 1 creates channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token1, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # User 1 Sends Message to channel
    send_http = requests.post(config.url + "/message/send/v1", json={'token': token1, 'channel_id': channel_id, 'message': MESSAGE})
    message_id = send_http.json()['message_id']
    # User 2 (no permissions) tries to remove User 1's message
    remove_http = requests.delete(config.url + "/message/remove/v1", json={'token': token2, 'message_id': message_id})
    assert(remove_http.status_code == INPUT_ERROR)

def test_remove_access_error_no_permissions():
    token1_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token1 = token1_http.json()['token']

    token2_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example2@gmail.com',
        'password': 'password',
        'name_first': 'Dave',
        'name_last': 'Cave'})
    token2 = token2_http.json()['token']
    # User 1 creates channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token1, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # User 1 Sends Message to channel
    send_http = requests.post(config.url + "/message/send/v1", json={'token': token1, 'channel_id': channel_id, 'message': MESSAGE})
    message_id = send_http.json()['message_id']

    # User  joins the channel
    join_req = requests.post(config.url + "/channel/join/v2", json={'token': token2, 'channel_id': channel_id})
    assert join_req.status_code == SUCCESS

    # User 2 (no permissions) tries to remove User 1's message
    remove_http = requests.delete(config.url + "/message/remove/v1", json={'token': token2, 'message_id': message_id})
    assert(remove_http.status_code == ACCESS_ERROR)

def test_remove_message_DM_no_permissions():
    # Create user 1
    token1_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token1 = token1_http.json()['token']

    token2_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example2@gmail.com',
        'password': 'password',
        'name_first': 'Dave',
        'name_last': 'Cave'})
    u_id2 = token2_http.json()['auth_user_id']
    token2 = token2_http.json()['token']

    # User 1 creates DM
    dm_http = requests.post(config.url + "/dm/create/v1", json={'token': token1, 'u_ids': [u_id2]})
    dm_id = dm_http.json()['dm_id']
    assert dm_http.status_code == SUCCESS

    # User 1 sends a Message in created DM
    send_http = requests.post(config.url + "/message/senddm/v1", json={'token': token1, 'dm_id': dm_id, 'message': MESSAGE})
    message_id = send_http.json()['message_id']
    assert send_http.status_code == SUCCESS

    # User 2 tries to remove sent message
    remove_http = requests.delete(config.url + "/message/remove/v1", json={'token': token2, 'message_id': message_id})
    assert remove_http.status_code == ACCESS_ERROR
