import json
import requests
import pytest
from src.other import clear_v1
from src import config
from tests.test_msg_helper import *
from src.auth import *
from src.notifications import *
from src.error import AccessError
from src.channels import *
from src.channel import *
from src.message import *
from src.dm import *

INPUT_ERROR = 400
ACCESS_ERROR = 403
SUCCESS = 200

@pytest.fixture(autouse=True)
def clear():
    requests.delete(config.url + "clear/v1")

def test_invalid_token():
    auth_register("email@gmail.com", "1234567", "jinny", "seo")
    response = requests.get(config.url + 'notifications/get/v1', params = {'token': 'invalid_token'})
    assert response.status_code == ACCESS_ERROR

def test_notif_joined_channel():
    user1 = auth_register('email@gmail.com', "1234567", "jinny", "seo")
    handle_str1 = find_user(user1['auth_user_id'])
    user2 = auth_register('email1@gmail.com', "12345671", "jinny1", "seo1")
    
    # Have user1 create a channel and invite user2 to it.
    channel1 = channel_create(user1['token'], 'Channel1', True)
    invite_channel(user1['token'],channel1['channel_id'], user2['auth_user_id'])

    r = requests.get(config.url + 'notifications/get/v1', params={
        'token': user2['token']
    })
    assert r.status_code == SUCCESS

    notifs_1 = r.json()
    details = channel_details(user1['token'], channel1['channel_id'])
    handle_str1 = details['all_members'][0]['handle_str']
    channel_name = details['name']

    assert notifs_1[0]['channel_id'] == channel1['channel_id']
    assert notifs_1[0]['dm_id'] == -1
    assert notifs_1[0]['notification_message'] == f"{handle_str1} added you to {channel_name}"

def test_notif_joined_dm():
    user1 = auth_register('email@gmail.com', "1234567", "jinny", "seo")
    user2 = auth_register('email1@gmail.com', "12345671", "jinny1", "seo1")
    
    # Have user1 create a channel and invite user2 to it.
    dm = dm_create(user1['token'], [user2["auth_user_id"]])

    r = requests.get(config.url + 'notifications/get/v1', params={
        'token': user2['token']
    })
    assert r.status_code == SUCCESS
    notifs_1 = r.json()

    details = dm_details(user1["token"], dm.json()['dm_id'])
    handle_str1 = details['members'][1]['handle_str']
    dm_name = details['name']

    assert notifs_1[0]['channel_id'] == -1
    assert notifs_1[0]['dm_id'] == dm.json()['dm_id']
    assert notifs_1[0]['notification_message'] == f"{handle_str1} added you to {dm_name}"

def test_send_msg_tag():
    user1 = auth_register('email@gmail.com', "1234567", "jinny", "seo")
    user2 = auth_register('email1@gmail.com', "12345671", "jinny1", "seo1")

    channel1 = channel_create(user1['token'], 'Channel1', True)
    join_channel(user2['token'], channel1['channel_id'])

    details = channel_details(user2['token'], channel1['channel_id'])
    handle_str1 = details['all_members'][0]['handle_str']
    handle_str2 = details['all_members'][1]['handle_str']
    channel_name = details['name']

    msg = f"tagging @{handle_str1}"
    send_message(user2['token'], channel1['channel_id'], msg)

    r = requests.get(config.url + 'notifications/get/v1', params={
        'token': user1['token']
    })
    assert r.status_code == SUCCESS
    notifs_1 = r.json()

    assert notifs_1[0]['channel_id'] == channel1['channel_id']
    assert notifs_1[0]['dm_id'] == -1
    assert notifs_1[0]['notification_message'] == f"{handle_str2} tagged you in {channel_name}: {msg[:20]}"

def test_send_dm_tag():
    user1 = auth_register('email@gmail.com', "1234567", "jinny", "seo")
    user2 = auth_register('email1@gmail.com', "12345671", "jinny1", "seo1")

    dm = dm_create(user2["token"], [user1["auth_user_id"]])
    details = dm_details(user2["token"], dm.json()['dm_id'])
    handle_str1 = details['members'][0]['handle_str']
    handle_str2 = details['members'][1]['handle_str']
    dm_name = details['name']

    msg = f"tagging @{handle_str1}"
    senddm(user2['token'], dm.json()['dm_id'], msg)

    r = requests.get(config.url + 'notifications/get/v1', params={
        'token': user1['token']
    })
    assert r.status_code == SUCCESS
    notifs_1 = r.json()

    assert notifs_1[0]['channel_id'] == -1
    assert notifs_1[0]['dm_id'] == dm.json()['dm_id']
    assert notifs_1[0]['notification_message'] == f"{handle_str2} tagged you in {dm_name}: {msg[:20]}"

    assert notifs_1[1]['channel_id'] == -1
    assert notifs_1[1]['dm_id'] == dm.json()['dm_id']
    assert notifs_1[1]['notification_message'] == f"{handle_str2} added you to {dm_name}"

def test_react_msg_in_channel():
    user1 = auth_register('email@gmail.com', "1234567", "jinny", "seo")
    user2 = auth_register('email1@gmail.com', "12345671", "jinny1", "seo1")

    channel1 = channel_create(user1['token'], 'Channel1', True)
    join_channel(user2['token'], channel1['channel_id'])

    details = channel_details(user1['token'], channel1['channel_id'])
    handle_str2 = details['all_members'][1]['handle_str']
    channel_name = details['name']

    msg = 'test for react'
    msg_id = send_message(user1['token'], channel1['channel_id'], msg)[
        'message_id']
    message_react(user2['token'], msg_id, 1)

    r = requests.get(config.url + 'notifications/get/v1', params={
        'token': user1['token']
    })
    assert r.status_code == SUCCESS
    notifs_1 = r.json()

    assert notifs_1[0]['channel_id'] == channel1['channel_id']
    assert notifs_1[0]['dm_id'] == -1
    assert notifs_1[0]['notification_message'] == f"{handle_str2} reacted to your message in {channel_name}"

def test_react_msg_in_dm():
    user1 = auth_register('email@gmail.com', "1234567", "jinny", "seo")
    user2 = auth_register('email1@gmail.com', "12345671", "jinny1", "seo1")

    dm1 = dm_create(user1['token'], [user2['auth_user_id']])

    details = dm_details(user2['token'], dm1.json()['dm_id'])
    handle_str2 = details['members'][0]['handle_str']
    dm_name = details['name']

    msg = 'test for react'
    msg_id = senddm(user1['token'], dm1.json()['dm_id'], msg).json()[
        'message_id']
    message_react(user2['token'], msg_id, 1)

    r = requests.get(config.url + 'notifications/get/v1', params={
        'token': user1['token']
    })
    assert r.status_code == SUCCESS
    notifs_1 = r.json()

    assert notifs_1[0]['channel_id'] == -1
    assert notifs_1[0]['dm_id'] == dm1.json()['dm_id']
    assert notifs_1[0]['notification_message'] == f"{handle_str2} reacted to your message in {dm_name}"

def test_over_20_notifications():
    user1 = auth_register('email@gmail.com', "1234567", "jinny", "seo")
    user2 = auth_register('email1@gmail.com', "12345671", "jinny1", "seo1")

    channel1 = channel_create(user1['token'], 'Channel1', True)
    join_channel(user2['token'], channel1['channel_id'])

    details = channel_details(user2['token'], channel1['channel_id'])
    handle_str1 = details['all_members'][0]['handle_str']
    handle_str2 = details['all_members'][1]['handle_str']
    channel_name = details['name']

    msg = f"tagging @{handle_str1}"
    for _ in range(21):
        (send_message(user2['token'], channel1['channel_id'], msg))

    r = requests.get(config.url + 'notifications/get/v1', params={
        'token': user1['token']
    })
    assert r.status_code == SUCCESS
    notifs_1 = r.json()

    assert notifs_1[0]['channel_id'] == channel1['channel_id']
    assert notifs_1[0]['dm_id'] == -1
    assert notifs_1[0]['notification_message'] == f"{handle_str2} tagged you in {channel_name}: {msg[:20]}"

def test_over_20_character_notification():
    user1 = auth_register('email@gmail.com', "1234567", "jinny", "seo")
    user2 = auth_register('email1@gmail.com', "12345671", "jinny1", "seo1")

    # Test in channel
    channel1 = channel_create(user1['token'], 'Channel1', True)
    join_channel(user2['token'], channel1['channel_id'])

    details = channel_details(user2['token'], channel1['channel_id'])
    handle_str1 = details['all_members'][0]['handle_str']
    handle_str2 = details['all_members'][1]['handle_str']
    channel_name = details['name']

    msg = f"tagging @{handle_str1}"*5
    send_message(user2['token'], channel1['channel_id'], msg)

    r = requests.get(config.url + 'notifications/get/v1', params={
        'token': user1['token']
    })
    assert r.status_code == SUCCESS
    notifs_1 = r.json()

    assert notifs_1[0]['channel_id'] == channel1['channel_id']
    assert notifs_1[0]['dm_id'] == -1
    assert notifs_1[0]['notification_message'] == f"{handle_str2} tagged you in {channel_name}: {msg[:20]}"

    # Test in dm
    dm = dm_create(user2["token"], [user1["auth_user_id"]])
    details = dm_details(user2["token"], dm.json()['dm_id'])
    handle_str1 = details['members'][0]['handle_str']
    handle_str2 = details['members'][1]['handle_str']
    dm_name = details['name']

    msg = f"tagging @{handle_str1}"*5
    senddm(user2['token'], dm.json()['dm_id'], msg)

    r = requests.get(config.url + 'notifications/get/v1', params={
        'token': user1['token']
    })
    assert r.status_code == SUCCESS
    notifs_1 = r.json()

    assert notifs_1[0]['channel_id'] == -1
    assert notifs_1[0]['dm_id'] == dm.json()['dm_id']
    assert notifs_1[0]['notification_message'] == f"{handle_str2} tagged you in {dm_name}: {msg[:20]}"

    assert notifs_1[1]['channel_id'] == -1
    assert notifs_1[1]['dm_id'] == dm.json()['dm_id']
    assert notifs_1[1]['notification_message'] == f"{handle_str2} added you to {dm_name}"

def test_user_not_in_channel():
    user1 = auth_register('email@gmail.com', "1234567", "jinny", "seo")
    user2 = auth_register('email1@gmail.com', "12345671", "jinny1", "seo1")

    channel1 = channel_create(user1['token'], 'Channel1', True)

    details = channel_details(user1['token'], channel1['channel_id'])
    handle_str1 = details['all_members'][0]['handle_str']

    msg = f"tagging @{handle_str1}"
    send_message(user2['token'], channel1['channel_id'], msg)

    r = requests.get(config.url + 'notifications/get/v1', params={
        'token': user1['token']
    })
    assert r.status_code == SUCCESS
    notifs_1 = r.json()

    assert notifs_1 == []
