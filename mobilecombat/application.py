#!/usr/bin/python
from gevent import monkey; monkey.patch_all()
import os
import sys
import traceback
from django.core.handlers.wsgi import WSGIHandler
from django.core.signals import got_request_exception
from django.core.management import call_command

sys.path.append('/home/cnaut/meandmyphone')
sys.path.append('/home/cnaut/meandmyphone/mobilecombat')
os.environ['DJANGO_SETTINGS_MODULE'] = 'mobilecombat.settings'

def exception_printer(sender, **kwargs):
    traceback.print_exc()


got_request_exception.connect(exception_printer)


application = WSGIHandler()
