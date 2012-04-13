import os, sys
sys.path.append('/usr/local/meandmyphone')
sys.path.append('/usr/local/meandmyphone/geppetto')
os.environ['DJANGO_SETTINGS_MODULE'] = 'geppetto.settings'

import django.core.handlers.wsgi

application = django.core.handlers.wsgi.WSGIHandler()
