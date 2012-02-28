#!/usr/bin/env python

PORT = 8000

import os
import sys

sys.path.append('/home/cnaut/meandmyphone')
sys.path.append('/home/cnaut/meandmyphone/mobilecombat')
os.environ['DJANGO_SETTINGS_MODULE'] = 'mobilecombat.settings'

import django.core.handlers.wsgi
application = django.core.handlers.wsgi.WSGIHandler()

from socketio import SocketIOServer

if __name__ == '__main__':
    print 'Listening on port %s and on port 843 (flash policy server)' % PORT
    SocketIOServer(('', PORT), application, resource="socket.io").serve_forever()
h

