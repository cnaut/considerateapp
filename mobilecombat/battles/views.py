import json

from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from django.db import models 
from battles.models import Battle

def home(request):
	return HttpResponse("Moble Combat Home")

@csrf_exempt
def storetext(request):
	data = request.raw_post_data
	data = json.loads(data)	
	battle = Battle.objects.create(text=data)
	battle.save()
	
	print data['name']

	return HttpResponse(data['name'])

@csrf_exempt
def wait(request):
	while True:	
		print "Hi"
