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
########## DM_CREATE_V1 TESTS ##########

# InputError when any u_id in u_ids does not refer to a valid user
def tests_invalid_u_id():
    user = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    response = requests.post(config.url + 'dm/create/v1', json={
            'token': user.json()["token"],
            'u_ids': [123],
        })
    assert response.status_code == INPUT_ERROR

# AccessError when an invalid token is given
def test_token_invalid():
    response = requests.post(config.url + '/dm/create/v1', json={
            'token': 'invalid_token',
            'u_ids': []})
    assert response.status_code == ACCESS_ERROR

# InputError when there are duplicate 'u_ids' in u_ids
def test_duplicate_u_ids():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_1_token = user_1.json()['token']

    user_2 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email2@gmail.com',
            'password': 'password',
            'name_first': 'Johns',
            'name_last': 'Smiths',
            })
    u_id_2 = user_2.json()['auth_user_id']

    response_1 = requests.post(config.url + 'dm/create/v1', json={
            'token': user_1_token,
            'u_ids': [u_id_2, u_id_2],
            })
    assert response_1.status_code == INPUT_ERROR

# valid test - create a dm with another u_id
def test_valid_create_dm():
    owner = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email@gmail.com',
            'password': 'password',
            'name_first': 'Johns',
            'name_last': 'Smiths',
        })
    owner_token = owner.json()["token"]
    user = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })

    user_1 = user.json()['auth_user_id']
    response = requests.post(config.url + 'dm/create/v1', json={
            'token': owner_token,
            'u_ids': [user_1],
        })
    dm_id = response.json()['dm_id']

    assert response.status_code == SUCCESS
    # Check if User 1 is in dm
    dm_list_http = requests.get(config.url + "dm/list/v1", params={'token': owner_token})
    dms = dm_list_http.json()['dms']
    assert(dms == [{'dm_id': 0, 'name': 'johnsmith, johnssmiths'}])


    assert response.json() ==  {
        'dm_id': dm_id,
    }


# Create DM, with users: user1 and user2
def test_valid_create_2_dm():
    owner = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email@gmail.com',
            'password': 'password',
            'name_first': 'Anton',
            'name_last': 'Ego',
        })
    assert owner.status_code == SUCCESS
    owner_token = owner.json()["token"]
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'Peter',
            'name_last': 'Smith',
        })
    assert user_1.status_code == SUCCESS

    user_1_token = user_1.json()["token"]
    user_1_id = user_1.json()["auth_user_id"]

    user_2 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email2@gmail.com',
            'password': 'password',
            'name_first': 'Peter',
            'name_last': 'Smith',
        })
    assert user_2.status_code == SUCCESS
    user_2_id = user_2.json()["auth_user_id"]

    response_1 = requests.post(config.url + 'dm/create/v1', json={
            'token': owner_token,
            'u_ids': [user_1_id, user_2_id],
        })
    assert response_1.status_code == SUCCESS

    # Check if User 1 is in dm
    dm_list_http = requests.get(config.url + "dm/list/v1", params={'token': owner_token})
    dms = dm_list_http.json()
    assert(dms == {'dms': [{'dm_id': 0, 'name': 'antonego, petersmith, petersmith0'}]})

    # Check if User 2 is in dm
    dm_list_http = requests.get(config.url + "dm/list/v1", params={'token': user_1_token})
    dms = dm_list_http.json()
    assert(dms == {'dms': [{'dm_id': 0, 'name': 'antonego, petersmith, petersmith0'}]})


# if no u_ids are specified, create a dm with only one user so the dm name should have one handle.
def test_no_uids():
    owner = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    owner_token = owner.json()['token']
    response = requests.post(config.url + 'dm/create/v1', json={
            'token': owner_token,
            'u_ids': [],
        })
    dm_id = response.json()['dm_id']

    assert response.status_code == SUCCESS

    # Check if User 1 is in dm
    dm_list_http = requests.get(config.url + "dm/list/v1", params={'token': owner_token})
    dms = dm_list_http.json()
    assert(dms == {'dms':[{'dm_id': dm_id, 'name': 'johnsmith'}]})

    assert response.json() == {
        'dm_id': response.json()['dm_id'],
        }