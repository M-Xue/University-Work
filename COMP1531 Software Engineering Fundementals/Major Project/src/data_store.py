'''
data_store.py

This contains a definition for a Datastore class which you should use to store your data.
You don't need to understand how it works at this point, just how to use it :)

The data_store variable is global, meaning that so long as you import it into any
python file in src, you can access its contents.

Example usage:

    from data_store import data_store

    store = data_store.get()
    print(store) # Prints { 'names': ['Nick', 'Emily', 'Hayden', 'Rob'] }

    names = store['names']

    names.remove('Rob')
    names.append('Jake')
    names.sort()

    print(store) # Prints { 'names': ['Emily', 'Hayden', 'Jake', 'Nick'] }
    data_store.set(store)
'''

## YOU SHOULD MODIFY THIS OBJECT BELOW
import pickle

initial_object = {
        'users' : [
            # {
            # 'u_id' : int
            # 'email' : string
            # 'name_first' : string
            # 'name_last'  : string
            # 'handle_str' : string
            # 'password' : string
            # 'permission_id' : int
            # 'profile_img_url' : string
            # }
        ],
        'channels' : [
            # {
            # 'channel_id' : int
            # 'name' : string
            # 'is_public' : boolean
            # 'owner_members' : []
            # 'all_members' : []
            # 'standup_active' : bool
            # 'standup_time_finish' : int (unix timestamp)
            # 'standup_start_user' : int
            # 'standup_messages' : [
            #   {
            #       'u_id' : int
            #       'message' : string
            #   }
            # ]
            # }
        ],
        'messages' : [
            # {
            # 'message_id': int
            # 'auth_user_id': int
            # 'message': string
            # 'time_sent': date
            # 'channel_id': int
            # 'reacts': [{
            #   'react_id': int
            #   'u_ids': []
            # }]
            # 'dm_id': None,
            # 'is_pinned': False
            # }
        ],
        'dm_messages' : [
            # {
            # 'message_id': int
            # 'auth_user_id': int
            # 'message': string
            # 'time_sent': date
            # 'channel_id': int
            # 'dm_id' : int
            # 'reacts': [{
            #   'react_id': int
            #   'u_ids': []
            # }]
            # 'is_pinned': False
            # }
        ],
        'dms' : [
            # {
            # 'dm_id' : int
            # 'name' : string
            # 'owners_id' : int
            # 'u_ids' : []
            # 'standup_active' : bool
            # 'standup_time_finish' : int (unix timestamp)
            # 'standup_start_user' : int
            # 'standup_messages' : [
            #   {
            #       'u_id' : int
            #       'message' : string
            #   }
            # ]
            # }

        ],
        'sessions' : [
            # {
            #   'session_id': int
            #   'auth_user_id': int
            # }
        ],
        'history' : [
            # {
            #     'command': str
            #     'auth_user_id': int
            #     'timestamp': integer
            #     'params': list
            # }
        ],
        'new_pwd_codes' : [
            # {
            #     'u_id': int,
            #     'code': int
            # }
        ],

    }

try:
    data = pickle.load(open("datastore.p", "rb"))
    initial_object = data
except Exception:
    initial_object = {
        'users' : [
            # {
            # 'u_id' : int
            # 'email' : string
            # 'name_first' : string
            # 'name_last'  : string
            # 'handle_str' : string
            # 'password' : string
            # 'permission_id' : int
            # 'profile_img_url' : string
            # }
        ],
        'channels' : [
            # {
            # 'channel_id' : int
            # 'name' : string
            # 'is_public' : boolean
            # 'owner_members' : []
            # 'all_members' : []
            # 'standup_active' : bool
            # 'standup_time_finish' : int (unix timestamp)
            # 'standup_start_user' : int
            # 'standup_messages' : [
            #   {
            #       'u_id' : int
            #       'message' : string
            #   }
            # ]
            # }
        ],
        'messages' : [
            # {
            # 'message_id': int
            # 'auth_user_id': int
            # 'message': string
            # 'time_sent': date
            # 'channel_id': int
            # }
        ],
        'dm_messages' : [
            # {
            # 'message_id': int
            # 'auth_user_id': int
            # 'message': string
            # 'time_sent': date
            # 'channel_id': int
            # 'dm_id' : int
            # }
        ],
        'dms' : [
            # {
            # 'dm_id' : int
            # 'name' : string
            # 'owners_id' : int
            # 'u_ids' : []
            # 'standup_active' : bool
            # 'standup_time_finish' : int (unix timestamp)
            # 'standup_start_user' : int
            # 'standup_messages' : [
            #   {
            #       'u_id' : int
            #       'message' : string
            #   }
            # ]
            # }
        ],
        'sessions' : [
            # {
            #   'session_id': int
            #   'auth_user_id': int
            # }
        ],
        'history' : [
            # {
            #     'command': str
            #     'auth_user_id': int
            #     'timestamp': integer
            #     'params': list
            # }
        ],
        'new_pwd_codes' : [
            # {
            #     'u_id': int,
            #     'code': int
            # }
        ],
    }



def save_datastore():
    '''
        Saves all data in data_store object into pickle file
    '''
    global initial_object
    try:
        with open('datastore.p', 'wb') as FILE:
            pickle.dump(initial_object, FILE)
    except FileNotFoundError:
        return
    return



## YOU SHOULD MODIFY THIS OBJECT ABOVE

## YOU ARE ALLOWED TO CHANGE THE BELOW IF YOU WISH
class Datastore:
    def __init__(self):
        self.__store = initial_object

    def get(self):
        return self.__store

    def set(self, store):
        if not isinstance(store, dict):
            raise TypeError('store must be of type dictionary')
        self.__store = store

print('Loading Datastore...')

global data_store
data_store = Datastore()
