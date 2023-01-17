from src.data_store import data_store
import os, shutil

def clear_v1():
    store = data_store.get()
    store['users'] = []
    store['channels'] = []
    store['messages'] = []
    store['dms'] = []
    store['dm_messages'] = []
    store['sessions'] = []
    store['history'] = []

    # Deletes all saved user profiles
    folder = 'src/images/profiles'
    for filename in os.listdir(folder):
        if filename != '.gitkeep':
            file_path = os.path.join(folder, filename)
            os.unlink(file_path)

    if os.path.exists("id_store.json"):
        os.remove("id_store.json")
        print('file removed')
    else:
        print("empty json file")

    data_store.set(store)
