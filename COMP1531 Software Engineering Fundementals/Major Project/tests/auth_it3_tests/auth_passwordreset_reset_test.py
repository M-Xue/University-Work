import pytest
from src.config import url

import requests

BASE_URL = url

def test_invalid_password():
    requests.delete(f"{BASE_URL}clear/v1")

    reset_pwd_response = requests.post(f"{BASE_URL}auth/passwordreset/reset/v1", json={"reset_code": "100000", "new_password ": "x"})
    assert reset_pwd_response.status_code == 400

    requests.delete(f"{BASE_URL}clear/v1")

def test_invalid_reset_code():
    requests.delete(f"{BASE_URL}clear/v1")

    reset_pwd_response = requests.post(f"{BASE_URL}auth/passwordreset/reset/v1", json={"reset_code": "0", "new_password ": "xxxxxxx"})
    assert reset_pwd_response.status_code == 400

    requests.delete(f"{BASE_URL}clear/v1")
