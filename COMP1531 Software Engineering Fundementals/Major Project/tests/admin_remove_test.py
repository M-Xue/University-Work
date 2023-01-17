import pytest
import requests
from src import config
SUCCESS = 200
INPUT_ERROR = 400
ACCESS_ERROR = 403
NONEXISTANT_UID = 999

@pytest.fixture(autouse=True)
def clear_data():
    requests.delete(config.url + "/clear/v1")

def test_admin_remove_channel():
    # First user is global owner (permission_id = 1)
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

    # Let user 1 make channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token1, 'name': 'Name', 'is_public': True})
    channel_id = channel_http.json()['channel_id']

    # User 2 joins the channel
    join_req = requests.post(config.url + "/channel/join/v2", json={'token': token2, 'channel_id': channel_id})
    assert join_req.status_code == SUCCESS

    # User 1 sends a message
    requests.post(config.url + "/message/send/v1", json={'token': token1, 'channel_id': channel_id, 'message': 'MESSAGE'})

    # User 2 sends a message
    requests.post(config.url + "/message/send/v1", json={'token': token2, 'channel_id': channel_id, 'message': 'MESSAGE'})

    # User 1 (seams owner) removes user 2 (member)
    remove_http = requests.delete(config.url + "/admin/user/remove/v1", json={'token': token1, 'u_id': u_id2})
    assert remove_http.status_code == SUCCESS

    # Check that sent message was replaced with 'Removed user'
    messages_http = requests.get(config.url + "/channel/messages/v2", params={'token': token1, 'channel_id': channel_id, 'start': 0})
    assert(messages_http.json()['messages'][0]['message'] == 'Removed user')

    # Check that there is only 1  member in the channel now
    details_http = requests.get(config.url + "/channel/details/v2", params={'token': token1, 'channel_id': channel_id})
    assert( len(details_http.json()['all_members']) == 1 )

    # Check that there is only 1 user left in users/all
    users_all_http = requests.get(config.url + "/users/all/v1", params={'token': token1})
    assert(len(users_all_http.json()['users']) == 1)

    # Check if profile is retrievable via user/profile but name_first is 'Removed' and name_last is 'user'
    profile_http = requests.get(config.url + "/user/profile/v1", params={'token': token1, 'u_id': u_id2})
    assert profile_http.status_code == SUCCESS
    assert profile_http.json()['user']['u_id'] == u_id2
    assert profile_http.json()['user']['name_first'] == 'Removed'
    assert profile_http.json()['user']['name_last'] == 'user'

    # Check to see if the email and handle is reusable
    token3_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example2@gmail.com', 
        'password': 'password',
        'name_first': 'Dave', 
        'name_last': 'Cave'})
    assert token3_http.status_code == SUCCESS

    # Check to see that token no longer works
    get_all_users_response = requests.get(config.url + "users/all/v1", params={'token': token2})
    assert get_all_users_response.status_code == 403



def test_admin_remove_channel_owner():
    # First user is global owner (permission_id = 1)
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

    # Let user 2 make channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token2, 'name': 'Name', 'is_public': True})
    channel_id = channel_http.json()['channel_id']

    # User 1 joins the channel
    join_req = requests.post(config.url + "/channel/join/v2", json={'token': token1, 'channel_id': channel_id})
    assert join_req.status_code == SUCCESS

    # User 2 sends a message
    requests.post(config.url + "/message/send/v1", json={'token': token2, 'channel_id': channel_id, 'message': 'MESSAGE'})
    
    # User 1 (seams owner) removes user 2 (member)
    remove_http = requests.delete(config.url + "/admin/user/remove/v1", json={'token': token1, 'u_id': u_id2})
    assert remove_http.status_code == SUCCESS

    # Check that sent message was replaced with 'Removed user'
    messages_http = requests.get(config.url + "/channel/messages/v2", params={'token': token1, 'channel_id': channel_id, 'start': 0})
    assert(messages_http.json()['messages'][0]['message'] == 'Removed user')

    # Check that there is only 1  member in the channel now
    details_http = requests.get(config.url + "/channel/details/v2", params={'token': token1, 'channel_id': channel_id})
    assert( len(details_http.json()['all_members']) == 1 )

    # Check that there is only 1 user left in users/all
    users_all_http = requests.get(config.url + "/users/all/v1", params={'token': token1})
    assert(len(users_all_http.json()['users']) == 1)

    # Check if profile is retrievable via user/profile but name_first is 'Removed' and name_last is 'user'
    profile_http = requests.get(config.url + "/user/profile/v1", params={'token': token1, 'u_id': u_id2})
    assert profile_http.status_code == SUCCESS
    assert profile_http.json()['user']['u_id'] == u_id2
    assert profile_http.json()['user']['name_first'] == 'Removed'
    assert profile_http.json()['user']['name_last'] == 'user'

    # Check to see if the email and handle is reusable
    token3_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example2@gmail.com', 
        'password': 'password',
        'name_first': 'Dave', 
        'name_last': 'Cave'})
    assert token3_http.status_code == SUCCESS

    # Check to see that token no longer works
    get_all_users_response = requests.get(config.url + "users/all/v1", params={'token': token2})
    assert get_all_users_response.status_code == 403

def test_admin_remove_dm():
    # First user is global owner (permission_id = 1)
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

    # Let user 1 make DM with user 2
    dm_http = requests.post(config.url + "/dm/create/v1", json={'token': token1, 'u_ids': [u_id2]})
    dm_id = dm_http.json()['dm_id']
    assert dm_http.status_code == SUCCESS

    # User 1 sends a Message in created DM
    send_http = requests.post(config.url + "/message/senddm/v1", json={'token': token1, 'dm_id': dm_id, 'message': 'Hello'})
    assert send_http.status_code == SUCCESS

    # User 2 sends a Message in created DM
    send_http = requests.post(config.url + "/message/senddm/v1", json={'token': token2, 'dm_id': dm_id, 'message': 'Hello'})
    assert send_http.status_code == SUCCESS
     
    # User 1 (seams owner) removes user 2 (member)
    remove_http = requests.delete(config.url + "/admin/user/remove/v1", json={'token': token1, 'u_id': u_id2})
    assert remove_http.status_code == SUCCESS

    # Check that sent message was replaced with 'Removed user'
    dm_messages_http = requests.get(config.url + "/dm/messages/v1", params={'token': token1, 'dm_id': dm_id, 'start': 0})
    assert(dm_messages_http.json()['messages'][0]['message'] == 'Removed user')

    # Check that there is only 1  member in the DM now
    dm_details_http = requests.get(config.url + "/dm/details/v1", params={'token': token1, 'dm_id': dm_id})
    assert( len(dm_details_http.json()['members']) == 1 )

    # Check that there is only 1 user left in users/all
    users_all_http = requests.get(config.url + "/users/all/v1", params={'token': token1})
    assert(len(users_all_http.json()['users']) == 1)

    # Check if profile is retrievable via user/profile but name_first is 'Removed' and name_last is 'user'
    profile_http = requests.get(config.url + "/user/profile/v1", params={'token': token1, 'u_id': u_id2})
    assert profile_http.status_code == SUCCESS
    assert profile_http.json()['user']['u_id'] == u_id2
    assert profile_http.json()['user']['name_first'] == 'Removed'
    assert profile_http.json()['user']['name_last'] == 'user'

    # Check to see if the email and handle is reusable
    token3_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example2@gmail.com', 
        'password': 'password',
        'name_first': 'Dave', 
        'name_last': 'Cave'})
    assert token3_http.status_code == SUCCESS

    # Check to see that token no longer works
    get_all_users_response = requests.get(config.url + "users/all/v1", params={'token': token2})
    assert get_all_users_response.status_code == 403

def test_remove_other_seams_owner():
    # First user is global owner (permission_id = 1)
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
    permission_http = requests.post(config.url + "/admin/userpermission/change/v1", json={'token': token1, 'u_id': u_id2, 'permission_id': 1})
    assert permission_http.status_code == SUCCESS

    # Let user 1 make channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token1, 'name': 'Name', 'is_public': True})
    channel_id = channel_http.json()['channel_id']

    # User 2 joins the channel
    join_req = requests.post(config.url + "/channel/join/v2", json={'token': token2, 'channel_id': channel_id})
    assert join_req.status_code == SUCCESS

    # User 2 sends a message
    requests.post(config.url + "/message/send/v1", json={'token': token2, 'channel_id': channel_id, 'message': 'MESSAGE'})
     
    # User 1 (seams owner) removes user 2 (also a seams owner)
    remove_http = requests.delete(config.url + "/admin/user/remove/v1", json={'token': token1, 'u_id': u_id2})
    assert remove_http.status_code == SUCCESS

    # Check that sent message was replaced with 'Removed user'
    messages_http = requests.get(config.url + "/channel/messages/v2", params={'token': token1, 'channel_id': channel_id, 'start': 0})
    assert(messages_http.json()['messages'][0]['message'] == 'Removed user')

    # Check that there is only 1  member in the channel now
    details_http = requests.get(config.url + "/channel/details/v2", params={'token': token1, 'channel_id': channel_id})
    assert( len(details_http.json()['all_members']) == 1 )

    # Check that there is only 1 user left in users/all
    users_all_http = requests.get(config.url + "/users/all/v1", params={'token': token1})
    assert( len(users_all_http.json()['users']) == 1 )

    # Check if profile is retrievable via user/profile but name_first is 'Removed' and name_last is 'user'
    profile_http = requests.get(config.url + "/user/profile/v1", params={'token': token1, 'u_id': u_id2})
    assert profile_http.status_code == SUCCESS
    assert profile_http.json()['user']['u_id'] == u_id2
    assert profile_http.json()['user']['name_first'] == 'Removed'
    assert profile_http.json()['user']['name_last'] == 'user'

    # Check to see if the email and handle is reusable
    token3_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example2@gmail.com', 
        'password': 'password',
        'name_first': 'Dave', 
        'name_last': 'Cave'})
    assert token3_http.status_code == SUCCESS

    # Check to see that token no longer works
    get_all_users_response = requests.get(config.url + "users/all/v1", params={'token': token2})
    assert get_all_users_response.status_code == 403

def test_remove_input_error_invalid_id():
    # First user is global owner (permission_id = 1)
    token1_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token1 = token1_http.json()['token']

    # User 1 (seams owner) tries to remove user 1 (the only global owner)
    remove_http = requests.delete(config.url + "/admin/user/remove/v1", json={'token': token1, 'u_id': NONEXISTANT_UID})
    assert remove_http.status_code == INPUT_ERROR

def test_remove_input_error_only_global_owner():
    # First user is global owner (permission_id = 1)
    token1_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token1 = token1_http.json()['token']
    u_id1 = token1_http.json()['auth_user_id']

    # User 1 (seams owner) tries to remove user 1 (the only global owner)
    remove_http = requests.delete(config.url + "/admin/user/remove/v1", json={'token': token1, 'u_id': u_id1})
    assert remove_http.status_code == INPUT_ERROR


def test_remove_access_error_unauthorised():
    # First user is global owner (permission_id = 1)
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
    token2 = token2_http.json()['token']

    # Third user is member (permission_id = 2)
    token3_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example3@gmail.com', 
        'password': 'password',
        'name_first': 'Steve', 
        'name_last': 'Mine'})
    u_id3 = token3_http.json()['auth_user_id']

    # User 2 (member) tries to remove user 3 (member)
    remove_http = requests.delete(config.url + "/admin/user/remove/v1", json={'token': token2, 'u_id': u_id3})
    assert remove_http.status_code == ACCESS_ERROR

def test_admin_remove_no_channel_or_dm():
    # First user is global owner (permission_id = 1)
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

    # Third user is member (permission_id = 2)
    token3_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example3@gmail.com', 
        'password': 'password',
        'name_first': 'Steve', 
        'name_last': 'Mine'})
    u_id3 = token3_http.json()['auth_user_id']

    # Let user 1 make channel
    requests.post(config.url + "/channels/create/v2", json={'token': token1, 'name': 'Name', 'is_public': True})

    # Let user 1 make DM with user 3
    requests.post(config.url + "/dm/create/v1", json={'token': token1, 'u_ids': [u_id3]})


    # User 1 (seams owner) removes user 2 (member)
    remove_http = requests.delete(config.url + "/admin/user/remove/v1", json={'token': token1, 'u_id': u_id2})
    assert remove_http.status_code == SUCCESS

    # Check that there is only 2 user left in users/all
    users_all_http = requests.get(config.url + "/users/all/v1", params={'token': token1})
    assert(len(users_all_http.json()['users']) == 2)

    # Check if profile is retrievable via user/profile but name_first is 'Removed' and name_last is 'user'
    profile_http = requests.get(config.url + "/user/profile/v1", params={'token': token1, 'u_id': u_id2})
    assert profile_http.status_code == SUCCESS
    assert profile_http.json()['user']['u_id'] == u_id2
    assert profile_http.json()['user']['name_first'] == 'Removed'
    assert profile_http.json()['user']['name_last'] == 'user'

    # Check to see if the email and handle is reusable
    token3_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example2@gmail.com', 
        'password': 'password',
        'name_first': 'Dave', 
        'name_last': 'Cave'})
    assert token3_http.status_code == SUCCESS

    # Check to see that token no longer works
    get_all_users_response = requests.get(config.url + "users/all/v1", params={'token': token2})
    assert get_all_users_response.status_code == 403