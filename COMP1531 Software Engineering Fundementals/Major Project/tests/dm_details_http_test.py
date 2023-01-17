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
########## DM_DETAILS_V1 TESTS ##########
# InputError when dm_id does not refer to a valid DM
def test_details_invalid_dm_id():
    owner = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    owner_token = owner.json()['token']

    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email2@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    token2 = user_1.json()['token']
    u_id2 = user_1.json()['auth_user_id']

    requests.post(config.url + 'dm/create/v1', json={
            'token': owner_token,
            'u_ids': [u_id2]})

    response = requests.get(config.url + "dm/details/v1", params={
            'token': token2,
            'dm_id': 123})
    assert response.status_code == INPUT_ERROR

# AccessError when dm_id is valid and the authorised user is not a member of the DM
def test_details_dm_id_not_in_dm():
    owner = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    owner_token = owner.json()['token']
    u_id_1 = owner.json()['auth_user_id']

    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
    })
    user_token = user_1.json()['token']

    dm_1 = requests.post(config.url + 'dm/create/v1', json={
            'token': owner_token,
            'u_ids': [u_id_1]})
    dm_id = dm_1.json()['dm_id']
    response = requests.get(config.url + "dm/details/v1", params={
            'token': user_token,
            'dm_id': dm_id})
    assert response.status_code == ACCESS_ERROR

# provide details of dm that only has the owner
def test_dm_details_only_owner():
    owner = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
            })
    token_1 = owner.json()['token']

    dm_1 = requests.post(config.url + 'dm/create/v1', json={
            'token': token_1,
            'u_ids': []})

    dm_id = dm_1.json()['dm_id']

    response = requests.get(config.url + "dm/details/v1", params={
            'token': token_1,
            'dm_id': dm_id})
    assert response.status_code == SUCCESS
    assert response.json()['name'] == 'johnsmith'
    assert response.json()['members'] == [{
            'u_id': 0,
            'email': 'email@gmail.com',
            'name_first': 'John',
            'name_last': 'Smith',
            'handle_str': 'johnsmith',
            }]


# provide details of dm of all the members
def test_dm_details_members():
    owner = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
            })
    owner_token = owner.json()["token"]
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
    })
    user_1_id = user_1.json()["auth_user_id"]
    user_2 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email2@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_2_id = user_2.json()["auth_user_id"]

    response_1 = requests.post(config.url + 'dm/create/v1', json={
        'token': owner_token,
        'u_ids': [user_1_id, user_2_id]})

    dm = response_1.json()['dm_id']

    response = requests.get(config.url + "dm/details/v1", params={
        'token': owner_token,
        'dm_id': dm})
    assert response.status_code == SUCCESS
    assert response.json()['name'] == 'johnsmith, johnsmith0, johnsmith1'
    assert response.json()['members'] == [{
            'u_id': 1,
            'email': 'email1@gmail.com',
            'name_first': 'John',
            'name_last': 'Smith',
            'handle_str': 'johnsmith0',
            }, {
            'u_id': 2,
            'email': 'email2@gmail.com',
            'name_first': 'John',
            'name_last': 'Smith',
            'handle_str': 'johnsmith1',
            },{
            'u_id': 0,
            'email': 'email@gmail.com',
            'name_first': 'John',
            'name_last': 'Smith',
            'handle_str': 'johnsmith',
            }]
