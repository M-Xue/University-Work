import pytest
import requests
from src import config
CHANNEL_NAME = "Name"
CHANNEL_IS_PUBLIC = True
MESSAGE = "Hello World!"
OWNER_PERMISSION_ID = 1
MEMBER_PERMISSION_ID = 2
SUCCESS = 200
INPUT_ERROR = 400
ACCESS_ERROR = 403


@pytest.fixture(autouse=True)
def clear_data():
    requests.delete(config.url + "/clear/v1")

def test_permission_change():
    # First user is seams owner (permission_id = 1)
    token1_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com', 
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token1 = token1_http.json()['token']
 
    # Second user is member (permission_id = 2)
    token2_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example2@gmail.com', 
        'password': 'password',
        'name_first': 'Dave', 
        'name_last': 'Cave'})
    token2 = token2_http.json()['token']
    u_id2 = token2_http.json()['auth_user_id']

    # User 1 (owner) changes permission of user 2 (member) to owner
    permission_http = requests.post(config.url + "/admin/userpermission/change/v1", json={'token': token1, 'u_id': u_id2, 'permission_id': OWNER_PERMISSION_ID})
    assert permission_http.status_code == SUCCESS

    # Then test to see if their permission changed by testing owner activites (removing)
    # User 1 creates channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token1, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # User 1 Sends Message to channel
    send_http = requests.post(config.url + "/message/send/v1", json={'token': token1, 'channel_id': channel_id, 'message': MESSAGE})
    message_id = send_http.json()['message_id']

    # User 2 joins the channel
    join_req = requests.post(config.url + "/channel/join/v2", json={'token': token2, 'channel_id': channel_id})
    assert join_req.status_code == SUCCESS

    # User 2 (now has owner permissions) removes User 1's message
    remove_http = requests.delete(config.url + "/message/remove/v1", json={'token': token2, 'message_id': message_id})
    assert remove_http.status_code == SUCCESS

    #Check that messages is now empty
    messages_http = requests.get(config.url + "/channel/messages/v2", params={'token': token1, 'channel_id': channel_id, 'start': 0})
    assert(messages_http.json()['messages'] == [])


def test_permission_change_1():
    # First user is seams owner (permission_id = 1)
    token1_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com', 
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token1 = token1_http.json()['token']
 
    # Second user is member (permission_id = 2)
    token2_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example2@gmail.com', 
        'password': 'password',
        'name_first': 'Dave', 
        'name_last': 'Cave'})
    token2 = token2_http.json()['token']
    u_id2 = token2_http.json()['auth_user_id']

    # User 1 (owner) changes permission of user 2 (member) to owner
    permission_http = requests.post(config.url + "/admin/userpermission/change/v1", json={'token': token1, 'u_id': u_id2, 'permission_id': OWNER_PERMISSION_ID})
    assert permission_http.status_code == SUCCESS

    # User 2  creates the channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token2, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # User 1 (global owner) calls for channel details
    details_http = requests.get(config.url + "/channel/details/v2", params={'token': token1, 'channel_id': channel_id})
    assert(details_http.status_code == SUCCESS)






def test_permission_input_error_invalid_user():
    # First user is seams owner (permission_id = 1)
    token1_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com', 
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token1 = token1_http.json()['token']

    # Change permission of nonexistant ID
    permission_http = requests.post(config.url + "/admin/userpermission/change/v1", json={'token': token1, 'u_id': 99999, 'permission_id': OWNER_PERMISSION_ID})
    assert permission_http.status_code == INPUT_ERROR

def test_permission_input_error_invalid_permission_id():
    # First user is seams owner (permission_id = 1)
    token1_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com', 
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token1 = token1_http.json()['token']
 
    # Second user is member (permission_id = 2)
    token2_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example2@gmail.com', 
        'password': 'password',
        'name_first': 'Dave', 
        'name_last': 'Cave'})
    u_id2 = token2_http.json()['auth_user_id']

    # User 1 (owner) changes permission of user 2 (member) to nonexistant permission ID
    permission_http = requests.post(config.url + "/admin/userpermission/change/v1", json={'token': token1, 'u_id': u_id2, 'permission_id': 99999})
    assert permission_http.status_code == INPUT_ERROR

def test_permission_input_error_existing_permission():
    # First user is seams owner (permission_id = 1)
    token1_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com', 
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token1 = token1_http.json()['token']
 
    # Second user is member (permission_id = 2)
    token2_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example2@gmail.com', 
        'password': 'password',
        'name_first': 'Dave', 
        'name_last': 'Cave'})
    u_id2 = token2_http.json()['auth_user_id']

    # User 1 (owner) changes permission of user 2 (member) to member
    permission_http = requests.post(config.url + "/admin/userpermission/change/v1", json={'token': token1, 'u_id': u_id2, 'permission_id': MEMBER_PERMISSION_ID})
    assert permission_http.status_code == INPUT_ERROR

def test_permission_input_error_only_owner():
    # First user is seams owner (permission_id = 1)
    token1_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com', 
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token1 = token1_http.json()['token']
    u_id1 = token1_http.json()['auth_user_id']

    # Change permissions of only global owner to member
    permission_http = requests.post(config.url + "/admin/userpermission/change/v1", json={'token': token1, 'u_id': u_id1, 'permission_id': MEMBER_PERMISSION_ID})
    assert permission_http.status_code == INPUT_ERROR


def test_permission_access_error():
    # First user is seams owner (permission_id = 1)
    requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com', 
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
 
    # Second user is member (permission_id = 2)
    token2_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example2@gmail.com', 
        'password': 'password',
        'name_first': 'Dave', 
        'name_last': 'Cave'})
    u_id2 = token2_http.json()['auth_user_id']

    # Third user is member (permission_id = 2)
    token3_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example3@gmail.com', 
        'password': 'password',
        'name_first': 'Steve', 
        'name_last': 'Mine'})
    token3 = token3_http.json()['token']

    # User 3 (member) tries to change permission of user 2
    permission_http = requests.post(config.url + "/admin/userpermission/change/v1", json={'token': token3, 'u_id': u_id2, 'permission_id': OWNER_PERMISSION_ID})
    assert permission_http.status_code == ACCESS_ERROR