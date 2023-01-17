import json
from src.data_store import data_store

def get_msg_id():
    '''
    Gets message id and stores in file so msg_id and dm_id does not duplicate

    Return Value:
        Returns {msg_id(int)} on condition when message is found
        Returns 0 on condition that message is not found
    '''
    try:
        msg_id = 0
        update = {}

        with open('id_store.json') as FILE:
            data = json.load(FILE)
            data['msg_id'] += 1
            msg_id = data['msg_id']
            print(f'msg_id: {msg_id}')
            update = data

        with open('id_store.json', 'w') as FILE:
            json.dump(update, FILE)
            print('new updated')
        return msg_id
        
    except FileNotFoundError:
        data = {
            'msg_id': 0,
            'session_id': 0
        }
        with open('id_store.json', 'w') as FILE:
            json.dump(data, FILE)
            print('new created')
        return 0