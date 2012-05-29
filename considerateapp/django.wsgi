import os, sys
sys.path.append('/usr/local/meandmyphone')
sys.path.append('/usr/local/meandmyphone/considerateapp')
os.environ['DJANGO_SETTINGS_MODULE'] = 'considerateapp.settings'

import django.core.handlers.wsgi

application = django.core.handlers.wsgi.WSGIHandler()
