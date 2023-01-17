import requests
from src import config

INPUT_ERROR = 400
ACCESS_ERROR = 403
SUCCESS = 200

def auth_register(email, password, name_first, name_last):
    response = requests.post(f'{config.url}auth/register/v2', json={
        'email': email,
        'password': password,
        'name_first': name_first,
        'name_last': name_last
    })
    user = response.json()
    return user

def auth_login(email, password):
    response = requests.post(f'{config.url}auth/login/v2', json={
        'email': email,
        'password': password,
    })
    user = response.json()
    return user

def channel_create(token, name, is_public):
    response = requests.post(f'{config.url}channels/create/v2', json={
        'token': token,
        'name': name,
        'is_public': is_public
    })
    create_ch = response.json()
    return create_ch

def channel_message(token, channel_id, start):
    response = requests.get(f'{config.url}channel/messages/v2', params={
        'token': token,
        'channel_id': channel_id,
        'start': start
    })
    return response

def send_message(token, channel_id, message):
    response = requests.post(f'{config.url}/message/send/v1', json={
        'token': token,
        'channel_id': channel_id,
        'message': message
    })
    message = response.json()
    return message

def dm_create(token, u_ids):
    response = requests.post(f'{config.url}dm/create/v1', json={
        'token': token,
        'u_ids': u_ids
    })
    return response

def senddm(token, dm_id, message):
    response = requests.post(f'{config.url}message/senddm/v1', json={
        'token': token,
        'dm_id': dm_id,
        'message': message
    })
    return response

def dm_messages(token, dm_id, start):
    response = requests.get(f'{config.url}dm/messages/v1', params={
        'token': token,
        'dm_id': dm_id,
        'start': start
    })
    return response

def channel_details(token, channel_id):
    channel_det = requests.get(f'{config.url}channel/details/v2', params={
        'token': token,
        'channel_id': channel_id,
    })
    payload = channel_det.json()
    return payload


def join_channel(token, channel_id):
    join = requests.post(f'{config.url}channel/join/v2', json={
        "token": token,
        "channel_id": channel_id,
    })
    payload = join.json()
    return payload


def dm_details(token, channel_id):
    dm_det = requests.get(f'{config.url}dm/details/v1', params={
        'token': token,
        'dm_id': channel_id,
    })
    payload = dm_det.json()
    return payload

def invite_channel(token, channel_id, u_id):
    join = requests.post(f'{config.url}channel/invite/v2', json={
        "token": token,
        "channel_id": channel_id,
        "u_id": u_id
    })
    payload = join.json()
    return payload
    
def message_react(token, message_id, react_id):
    '''
    React to message
    '''
    response = requests.post(f'{config.url}message/react/v1', json={
        'token': token,
        'message_id': message_id,
        'react_id': react_id
    })
    return response

def message_pin(token, message_id):
    '''
    Pins a message
    '''
    response = requests.post(f'{config.url}message/pin/v1', json={
        'token': token,
        'message_id': message_id,
    })
    return response

def channel_join(token, channel_id):
    '''
    Users joins a channel
    '''
    join_request = requests.post(f'{config.url}channel/join/v2', json={
        'token': token,
        'channel_id': channel_id
    })
    return join_request

