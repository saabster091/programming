import pandas as pd
import csv
from mcl_cli import *
import os


SAMPLE_TASK = 'Sample Task'
SAMPLE_GROUP = 'Sample'
SAMPLE_GROUP_PATH = TASKS_PATH+'/'+SAMPLE_GROUP
SAMPLE_TASK_PATH = SAMPLE_GROUP_PATH+'/'+SAMPLE_TASK


def add_sample_task():
    add_task(group=SAMPLE_GROUP,
             name=SAMPLE_TASK,
             due_date='11-13-2013')


def append_time_to_sample_task():
    with open(SAMPLE_TASK, 'a+') as task_file:
        writer = csv.writer(task_file, delimiter='\n')
        writer.writerow(['37'])


def clear_sample_task():
    os.remove(SAMPLE_TASK_PATH)
    if not os.path.exists(SAMPLE_TASK_PATH):
        print 'clear_sample_task FAILED!'


def add_sample_group():
    add_group(SAMPLE_GROUP)
    if not os.path.exists(SAMPLE_GROUP_PATH):
        print 'add_sample_group FAILED!'


def delete_sample_group():
    delete_group(SAMPLE_GROUP)
    if os.path.exists(SAMPLE_GROUP_PATH):
        print 'delete_sample_group FAILED!'


def test_ls():
    print ls()
    print ls(SAMPLE_GROUP)


if __name__ == "__main__":
    add_sample_group()
    add_sample_task()
    #append_to_sample_task()
    test_ls()
    #add_sample_group()
    #delete_sample_group()