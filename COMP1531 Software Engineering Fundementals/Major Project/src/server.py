from pstats import Stats
import sys
import signal
from src.data_store import data_store, save_datastore
from json import dumps
from flask import Flask, request, send_from_directory
from flask_cors import CORS
from src import config
from src.dm import dm_leave_v1, dm_list_v1, dm_create_v1, dm_remove_v1, dm_details_v1, dm_messages_v1
from src.channels import channels_create_v1, channels_list_v1, channels_listall_v1
from src.security_helper import generate_jwt, decode_jwt, check_valid_token
from src.auth import auth_register_v1, auth_login_v1, send_recovery_code, reset_password_code_input
from src.other import clear_v1
from src.message import message_send_v1, message_remove_v1, message_edit_v1, message_senddm_v1, message_sendlater_v1, message_sendlaterdm_v1, message_react_v1, message_unreact_v1, message_pin_v1, message_unpin_v1, message_share_v1
from src.channel import channel_addowner_v1, channel_join_v1, channel_invite_v1, channel_details_v1, channel_messages_v1, channel_leave_v1, channel_removeowner_v1
from src.user import users_all, user_profile, set_name, set_email, set_handle, save_profile_img
from src.security_helper import generate_jwt
from src.auth import auth_register_v1, auth_login_v1, logout
from src.admin import admin_userpermission_change_v1, admin_user_remove_v1
from src.stats import user_stats_v1, users_stats_v1
from src.other import clear_v1
from src.notifications import notifications_get_v1
from src.standup import standup_start_v1, standup_active_v1, standup_send_v1
from src.search import search_v1

def quit_gracefully(*args):
    '''For coverage'''
    exit(0)

def defaultHandler(err):
    response = err.get_response()
    print('response', err, err.get_response())
    response.data = dumps({
        "code": err.code,
        "name": "System Error",
        "message": err.get_description(),
    })
    response.content_type = 'application/json'
    return response

APP = Flask(__name__)
CORS(APP)

APP.config['TRAP_HTTP_EXCEPTIONS'] = True
APP.register_error_handler(Exception, defaultHandler)

#### NO NEED TO MODIFY ABOVE THIS POINT, EXCEPT IMPORTS


# Auth
@APP.route('/auth/register/v2', methods=['POST'])
def auth_register_v2():
    details = request.get_json()
    user_id = auth_register_v1(details['email'], details['password'], details['name_first'], details['name_last'])
    token = str(generate_jwt(user_id['auth_user_id']))
    save_datastore()
    return { 'token': token, 'auth_user_id': user_id['auth_user_id']  }

@APP.route('/auth/login/v2', methods=['POST'])
def auth_login_v2():
    details = request.get_json()
    user_id = auth_login_v1(details['email'], details['password'])
    token = str(generate_jwt(user_id['auth_user_id']))
    save_datastore()
    return { 'token': token, 'auth_user_id': user_id['auth_user_id'] }

@APP.route('/auth/logout/v1', methods=['POST'])
def auth_logout_v1():
    details = request.get_json()
    check_valid_token(details['token'])
    logout(details['token'])
    save_datastore()
    return {}

# Password Reset Email Code Request
@APP.route('/auth/passwordreset/request/v1', methods=['POST'])
def reset_email_request():
    details = request.get_json()
    email = details['email']
    send_recovery_code(email)
    return {} 

# Password Reset Code Input
@APP.route('/auth/passwordreset/reset/v1', methods=['POST'])
def reset_code_input():
    details = request.get_json()
    reset_code = details['reset_code']
    new_password  = details['new_password ']
    reset_password_code_input(reset_code, new_password)
    return {} 

# Upload Profile Picture
@APP.route('/user/profile/uploadphoto/v1', methods=['POST'])
def upload_profile_image():
    details = request.get_json()
    save_profile_img(details['token'], details['img_url'], details['x_start'], details['y_start'], details['x_end'], details['y_end'])
    return {} 

# Serve Profile Picture
@APP.route('/images/<path:path>')
def serve_profile_image(path):
    return send_from_directory('images', path)

# Clear
@APP.route('/clear/v1', methods=['DELETE'])
def clear_v2():
    clear_v1()
    save_datastore()
    return {}

# Get User
@APP.route('/users/all/v1', methods=['GET'])
def get_all_users():
    token = request.args.get('token')
    check_valid_token(token)
    save_datastore()
    return users_all(token)

@APP.route('/user/profile/v1', methods=['GET'])
def get_user():
    token = request.args.get('token')
    check_valid_token(token)
    u_id = request.args.get('u_id')
    save_datastore()
    return user_profile(token, u_id)

# Edit User Details
@APP.route('/user/profile/setemail/v1', methods=['PUT'])
def edit_user_email():
    details = request.get_json()
    check_valid_token(details['token'])
    save_datastore()
    return set_email(details['token'], details['email'])

@APP.route('/user/profile/setname/v1', methods=['PUT'])
def edit_user_name():
    details = request.get_json()
    check_valid_token(details['token'])
    save_datastore()
    return set_name(details['token'], details['name_first'], details['name_last'])

@APP.route('/user/profile/sethandle/v1', methods=['PUT'])
def edit_user_handle():
    details = request.get_json()
    check_valid_token(details['token'])
    save_datastore()
    return set_handle(details['token'], details['handle_str'])

# Channels create
@APP.route('/channels/create/v2', methods=['POST'])
def channels_create_v2():
    data = request.get_json()
    token = data['token']
    check_valid_token(token)
    auth_user_id = decode_jwt(token)['auth_user_id']
    channel_id = channels_create_v1(auth_user_id, data['name'], data['is_public'])
    save_datastore()
    return dumps(channel_id)

# Channels list
@APP.route('/channels/list/v2', methods=['GET'])
def channels_list_v2():
    data = request.args
    token = data['token']
    check_valid_token(token)
    auth_user_id = decode_jwt(token)['auth_user_id']
    channels = channels_list_v1(auth_user_id)
    save_datastore()
    return dumps(channels)

# Channel listall
@APP.route('/channels/listall/v2', methods=['GET'])
def channels_listall_http_v2():
    token = request.args.get("token")
    check_valid_token(token)
    auth_user_id = decode_jwt(token)['auth_user_id']
    channels_list = channels_listall_v1(auth_user_id)
    save_datastore()
    return channels_list

# Channel Join
@APP.route('/channel/join/v2', methods=['POST'])
def channel_join_v2():
    input = request.get_json()
    token = input['token']
    check_valid_token(token)
    auth_user_id = decode_jwt(token)['auth_user_id']
    channel_join_v1(auth_user_id, input['channel_id'])
    save_datastore()
    return {}

# Channel Invite
@APP.route('/channel/invite/v2', methods=['POST'])
def channel_invite_v2():
    print(data_store.get())
    input = request.get_json()
    token = input['token']
    check_valid_token(token)
    auth_user_id = decode_jwt(token)['auth_user_id']
    channel_invite_v1(auth_user_id, input['channel_id'], input['u_id'])
    save_datastore()
    return {}

# Channel Details
@APP.route('/channel/details/v2', methods=['GET'])
def channel_details_http_v2():
    token = request.args.get("token")
    check_valid_token(token)
    channel_id = request.args.get("channel_id", type=int)
    auth_user_id = decode_jwt(token)['auth_user_id']
    details = channel_details_v1(auth_user_id, channel_id)
    save_datastore()
    return details

# Channel Messages
@APP.route('/channel/messages/v2', methods=['GET'])
def channels_messages_v2():
    data = request.args
    token = data['token']
    check_valid_token(token)
    auth_user_id = decode_jwt(token)['auth_user_id']
    message = channel_messages_v1(auth_user_id, int(data['channel_id']), int(data['start']))
    save_datastore()
    return dumps(message)

# Send Messages
@APP.route("/message/send/v1", methods=["POST"])
def message_send():
    data = request.get_json()
    token = data['token']
    check_valid_token(token)
    message = message_send_v1(data['token'], int(data['channel_id']), data['message'])
    save_datastore()
    return dumps(message)

# Remove Messages
@APP.route("/message/remove/v1", methods=["DELETE"])
def message_remove_http_v1():
    data = request.get_json()
    token = data["token"]
    check_valid_token(token)
    message_id = int(data["message_id"])
    message_remove_v1(token, message_id)
    save_datastore()
    return {}

# Edit Messages
@APP.route("/message/edit/v1", methods=["PUT"])
def message_edit():
    data = request.get_json()
    token = data['token']
    check_valid_token(token)
    message = message_edit_v1(data['token'], int(data['message_id']), data['message'])
    save_datastore()
    return dumps(message)

# Send dm
@APP.route("/message/senddm/v1", methods=["POST"])
def message_senddm():
    data = request.get_json()
    token = data['token']
    check_valid_token(token)
    message = message_senddm_v1(data['token'], int(data['dm_id']), data['message'])
    save_datastore()
    return dumps(message)

# Send later
@APP.route("/message/sendlater/v1", methods=["POST"])
def message_sendlater():
    data = request.get_json()
    token = data['token']
    check_valid_token(token)
    message = message_sendlater_v1(data['token'], int(data['channel_id']), data['message'], int(data['time_sent']))
    return dumps(message)

# Send dm later
@APP.route("/message/sendlaterdm/v1", methods=["POST"])
def message_sendlaterdm():
    data = request.get_json()
    token = data['token']
    check_valid_token(token)
    message = message_sendlaterdm_v1(data['token'], int(data['dm_id']), data['message'], int(data['time_sent']))
    return dumps(message)

# Notifs
@APP.route('/notifications/get/v1', methods=['GET'])
def notifications():
    data = request.args
    token = data['token']
    data = notifications_get_v1(token)
    return dumps(data)

# Channel leave
@APP.route('/channel/leave/v1', methods=['POST'])
def channel_leave():
    input = request.get_json()
    channel_leave_v1(input['token'], input['channel_id'])
    save_datastore()
    return {}

# Channel addowner
@APP.route('/channel/addowner/v1', methods=['POST'])
def channel_addowner():
    input = request.get_json()
    channel_addowner_v1(input['token'], input['channel_id'], input['u_id'])
    save_datastore()
    return {}

# Channel removeowner
@APP.route('/channel/removeowner/v1', methods=['POST'])
def channel_removeowner():
    input = request.get_json()
    channel_removeowner_v1(input['token'], input['channel_id'], input['u_id'])
    save_datastore()
    return {}

# Change permissions
@APP.route("/admin/userpermission/change/v1", methods=['POST'])
def admin_userpermission_change_http_v1():
    data = request.get_json()
    token = data['token']
    u_id = int(data['u_id'])
    permission_id = int(data['permission_id'])
    admin_userpermission_change_v1(token, u_id, permission_id)
    save_datastore()
    return {}

@APP.route("/admin/user/remove/v1", methods=['DELETE'])
def admin_user_remove_http_v1():
    data = request.get_json()
    token = data['token']
    u_id = int(data['u_id'])
    admin_user_remove_v1(token, u_id)
    save_datastore()
    return {}

# Direct messages create
@APP.route('/dm/create/v1', methods=['POST'])
def dm_create():
    data = request.get_json()
    dm_id = dm_create_v1(data['token'], data['u_ids'])
    save_datastore()
    return dumps(dm_id)

# Direct messages list
@APP.route('/dm/list/v1', methods=['GET'])
def dm_list():
    data = request.args
    token = data['token']
    check_valid_token(token)
    data = dm_list_v1(token)
    save_datastore()
    return dumps(data)

# Direct messages remove
@APP.route('/dm/remove/v1', methods=['DELETE'])
def dm_remove():
    data  = request.get_json()
    dm_remove_v1(data['token'], data['dm_id'])
    save_datastore()
    return {}

# Direct messages details
@APP.route('/dm/details/v1', methods=['GET'])
def dm_details():
    data  = request.args
    token = data['token']
    check_valid_token(token)
    dm_id = int(data['dm_id'])
    details = dm_details_v1(token, dm_id)
    save_datastore()
    return dumps(details)

# Dm Leave
@APP.route('/dm/leave/v1', methods=['POST'])
def dm_leave():
    input = request.get_json()
    dm_leave_v1(input['token'], input['dm_id'])
    save_datastore()
    return {}

# Dm Messages
@APP.route('/dm/messages/v1', methods=['GET'])
def dm_message():
    data = request.args
    token = data['token']
    check_valid_token(token)
    dm = dm_messages_v1(data['token'], int(data['dm_id']), int(data['start']))
    save_datastore()
    return dumps(dm)

# Message React
@APP.route('/message/react/v1', methods=['POST'])
def react_message():
    data = request.get_json()
    token = data['token']
    message_id = int(data['message_id'])
    react_id = int(data['react_id'])
    react = message_react_v1(token, message_id, react_id)
    save_datastore()
    return dumps(react)

# Message Unreact
@APP.route('/message/unreact/v1', methods=['POST'])
def unreact_message():
    data = request.get_json()
    token = data['token']
    message_id = int(data['message_id'])
    react_id = int(data['react_id'])
    react = message_unreact_v1(token, message_id, react_id)
    save_datastore()
    return dumps(react)

# Message Pin
@APP.route('/message/pin/v1', methods=['POST'])
def pin_message():
    data = request.get_json()
    token = data['token']
    message_id = int(data['message_id'])
    pin = message_pin_v1(token, message_id)
    save_datastore()
    return dumps(pin)

# Message Unpin
@APP.route('/message/unpin/v1', methods=['POST'])
def unpin_message():
    data = request.get_json()
    token = data['token']
    message_id = int(data['message_id'])
    pin = message_unpin_v1(token, message_id)
    save_datastore()
    return dumps(pin)


# Message Share
@APP.route('/message/share/v1', methods=['POST'])
def share_message():
    data = request.get_json()
    token = data['token']
    og_message_id = int(data['og_message_id'])
    message = str(data['message'])
    channel_id = int(data['channel_id'])
    dm_id = int(data['dm_id'])
    check_valid_token(token)
    share = message_share_v1(token, og_message_id, message, channel_id, dm_id)
    return dumps(share)

# User Stats
@APP.route('/user/stats/v1', methods=['GET'])
def user_stats():
    token = request.args.get("token")
    check_valid_token(token)
    stats = user_stats_v1(token)
    return dumps(stats)

# Users Stats
@APP.route('/users/stats/v1', methods=['GET'])
def users_stats():
    token = request.args.get("token")
    check_valid_token(token)
    stats = users_stats_v1(token)
    return dumps(stats)

# Standup start   
@APP.route('/standup/start/v1', methods=['POST'])
def standup_start():
    input = request.get_json()
    standup = standup_start_v1(input['token'], input['channel_id'], input['length'])
    save_datastore()
    return dumps(standup)

# Standup active   
@APP.route('/standup/active/v1', methods=['GET'])
def standup_active():
    input = request.args
    standup = standup_active_v1(input['token'], int(input['channel_id']))
    save_datastore()
    return dumps(standup)

# Standup send   
@APP.route('/standup/send/v1', methods=['POST'])
def standup_sned():
    input = request.get_json()
    standup = standup_send_v1(input['token'], input['channel_id'], input['message'])
    save_datastore()
    return dumps(standup)

# Search
@APP.route('/search/v1', methods=['GET'])
def search():
    input = request.args
    search = search_v1(input['token'], input['query_str'])
    return dumps(search)


#### NO NEED TO MODIFY BELOW THIS POINT

if __name__ == "__main__":
    signal.signal(signal.SIGINT, quit_gracefully) # For coverage
    APP.run(port=config.port) # Do not edit this port
