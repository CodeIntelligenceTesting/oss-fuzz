# Copyright 2021 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
"""Utility module. Based on utils.ts."""
import logging

from github_actions_toolkit.artifact import config_variables

MAX_API_ATTEMPTS = 5
SLEEP_TIME = 1
INVALID_ARTIFACT_FILEPATH_CHARACTERS = [
    '"',
    ':',
    '<',
    '>',
    '|',
    '*',
    '?',
]
INVALID_ARTIFACT_NAME_CHARACTERS = ['\\', '/'
                                   ] + INVALID_ARTIFACT_FILEPATH_CHARACTERS

# !!! Convert exceptions to special kind.


def get_proper_retention(retention, retention_setting):
  """Checks that |retention| is a normal value and replaces it with
  |retention_setting| if set."""
  if retention < 0:
    raise Exception('Invalid retention, minimum value is 1.')

  if retention_setting:
    retention_setting = int(retention_setting)
    if retention_setting < retention:
      logging.warning(
          'Retention days is greater than max allowed by the repository.'
          ' Reducing retention to %d days', retention)
      retention = retention_setting
  return retention


def check_artifact_name(name):
  """utils.js checkArtifactName."""
  for invalid_char in INVALID_ARTIFACT_NAME_CHARACTERS:
    if invalid_char in name:
      raise Exception(
          ('Artifact name is invalid: {name}. '
           'Contains char: "{invalid_char}. '
           'Invalid chars are: {invalid_artifact_name_characters}.').format(
               name=name,
               invalid_char=invalid_char,
               invalid_artifact_name_characters=INVALID_ARTIFACT_NAME_CHARACTERS
           ))


def check_artifact_file_path(artifact_file_path):
  """Raises an exception if |artifact_file| is invalid."""
  if not artifact_file_path:
    raise Exception('Artifact file path does not exist', artifact_file_path)

  for invalid_char in INVALID_ARTIFACT_NAME_CHARACTERS:
    if invalid_char in artifact_file_path:
      raise Exception(
          ('Artifact path: {artifact_file_path} is invalid, contains '
           'invalid char: {invalid_char}.').format(
               artifact_file_path=artifact_file_path,
               invalid_char=invalid_char))


def get_content_range(start, end, total):
  """Returns the content range for an HTTP request."""
  return 'bytes {start}-{end}/{total}'.format(start=start, end=end, total=total)


def _get_http_request_headers():
  auth_token = config_variables.get_runtime_token()
  authorization = 'Bearer {auth_token}'.format(auth_token=auth_token)
  return {
      'Authorization': authorization
  }


def get_upload_headers(  # pylint: disable=too-many-arguments
    content_type=None,
    is_keep_alive=False,
    is_gzip=None,
    uncompressed_length=None,
    content_length=None,
    content_range=None):
  """utils.js"""
  request_options = _get_http_request_headers()
  api_version = get_api_version()
  request_options['Accept'] = (
      'application/json;api-version={api_version}'.format(
          api_version=api_version))

  if content_type:
    request_options['Content-Type'] = content_type

  if is_keep_alive:
    request_options['Connection'] = 'Keep-Alive'
    request_options['Keep-Alive'] = '10'

  if is_gzip:
    assert uncompressed_length is not None
    request_options['Content-Encoding'] = 'gzip'
    request_options['x-tfs-filelength'] = str(uncompressed_length)

  if content_length:
    request_options['Content-Length'] = str(content_length)

  if content_range:
    request_options['Content-Range'] = content_range

  return request_options


def get_api_version():
  """utils.js"""
  return '6.0-preview'


def get_artifact_url():
  """utils.js"""
  runtime_url = config_variables.get_runtime_url()
  work_flow_run_id = config_variables.get_work_flow_run_id()
  api_version = get_api_version()
  return (('{runtime_url}_apis/pipelines/workflows/{work_flow_run_id}/artifacts'
           '?api-version={api_version}').format(
               runtime_url=runtime_url,
               work_flow_run_id=work_flow_run_id,
               api_version=api_version))
