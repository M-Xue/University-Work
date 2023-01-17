import pytest
import requests
from src import config

SUCCESS = 200
INPUT_ERROR = 400
ACCESS_ERROR = 403

@pytest.fixture(autouse=True)
def clear_data():
    requests.delete(config.url + "clear/v1")

########## DM_LEAVE_V1 TESTS ##########

# Test for normal functioning 
def test_dm_leave():
    # Create User 1
    token1_http = requests.post(config.url + "auth/register/v2", json={
        'email': 'example1@gmail.com', 
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith',
    })
    token1 = token1_http.json()['token']

    # Create User 2
    token2_http = requests.post(config.url + "auth/register/v2", json={
        'email': 'example2@gmail.com',
        'password': 'password',
        'name_first': 'Dave',
        'name_last': 'Cave',
    })
    token2 = token2_http.json()['token']
    u_id2 = token2_http.json()['auth_user_id']

    # Create DM, with users: user1 and user2
    dm_create_http = requests.post(config.url + "dm/create/v1", json={'token': token1, 'u_ids':[u_id2]})
    dm_id = dm_create_http.json()['dm_id']

    # Check if User 1 is in dm
    dm_list_http = requests.get(config.url + "dm/list/v1", params={'token': token1})
    dms = dm_list_http.json()
    assert(dms == {'dms':[{'dm_id': dm_id, 'name': 'davecave, johnsmith'}]})

    # Check if User 2 is in dm
    dm_list_http = requests.get(config.url + "dm/list/v1", params={'token': token2})
    dms = dm_list_http.json()
    assert(dms == {'dms':[{'dm_id': dm_id, 'name': 'davecave, johnsmith'}]})

    # User 1 leaves the DM
    dm_leave_http = requests.post(config.url + "dm/leave/v1", json={'token': token1, 'dm_id': dm_id})
    assert dm_leave_http.status_code == SUCCESS

    # Check if User 1 is still in DM via dm/list
    dm_list_http = requests.get(config.url + "dm/list/v1", params={'token': token1})
    dms = dm_list_http.json()
    assert(dms == {'dms':[]} )

    # User 2 leaves the DM
    dm_leave_http = requests.post(config.url + "dm/leave/v1", json={'token': token2, 'dm_id': dm_id})
    assert dm_leave_http.status_code == SUCCESS

    # Check if User 2 is still in DM via dm/list
    dm_list_http = requests.get(config.url + "dm/list/v1", params={'token': token2})
    dms = dm_list_http.json()
    assert(dms == {'dms':[]} )

# Test for InputError when dm_id does not refer to a valid dm
def test_leave_input_error():
    token1_http = requests.post(config.url + "auth/register/v2", json={
        'email': 'example1@gmail.com', 
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith',
    })
    token1 = token1_http.json()['token']

    token2_http = requests.post(config.url + "auth/register/v2", json={
        'email': 'example2@gmail.com',
        'password': 'password',
        'name_first': 'Dave',
        'name_last': 'Cave',
    })
    token2 = token2_http.json()['token']
    u_id2 = token2_http.json()['auth_user_id']

    # Create DM, with users: user1 and user2
    requests.post(config.url + "dm/create/v1", json={'token': token1, 'u_ids':[u_id2]})

    # User 2 tries to leave non existant DM
    dm_leave_http = requests.post(config.url + "dm/leave/v1", json={'token': token2, 'dm_id': 123})
    assert(dm_leave_http.status_code == INPUT_ERROR)

# Test for AccessError when dm_id is valid but user is not part of channel
def test_leave_access_error():
    # Create User 1
    user1 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email1@gmail.com',
        'password': 'password1',
        'name_first': 'first1',
        'name_last': 'last1',
    })
    token1 = user1.json()['token']
    u_id1 = user1.json()['auth_user_id']

    # Create User 2
    user2 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email2@gmail.com',
        'password': 'password2',
        'name_first': 'first2',
        'name_last': 'last2',
    })
    token2 = user2.json()['token']

    # Create DM with only users: user 1
    dm = requests.post(config.url + "dm/create/v1", json={'token': token1, 'u_ids' : [u_id1]})
    dm_id = dm.json()['dm_id']
    # Make User 2 try to leave that DM
    leave_request = requests.post(config.url + "dm/leave/v1", json={'token': token2, 'dm_id': dm_id})
    # AccessError
    assert leave_request.status_code == ACCESS_ERROR

# Test for AccessErorr when an invalid token is given
def test_invalid_token():
    # Create user
    user = requests.post(config.url + "/auth/register/v2", json={
        'email': 'email@gmail.com',
        'password': 'password',
        'name_first': 'first',
        'name_last': 'last',
    })
    token = user.json()['token']
    u_id = user.json()['auth_user_id']
    
    # Create dm
    dm = requests.post(config.url + "dm/create/v1", json={'token': token, 'u_ids' : [u_id]})
    dm_id = dm.json()['dm_id']

    # Invalid token tries to leave dm
    leave_request = requests.post(config.url + "dm/leave/v1", json={'token': "invalid", 'dm_id': dm_id})
    assert leave_request.status_code == ACCESS_ERROR 
    