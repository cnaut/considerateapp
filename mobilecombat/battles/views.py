import json

from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from django.db import models 
from battles.models import Battle
from battles.models import User 

def home(request):
	return HttpResponse("Moble Combat Home")

@csrf_exempt
def adduser(request):
	data = request.raw_post_data
	data = json.loads(data)	
	
	user = User(name=data['name'], photo=request.FILES['image'])
	user.save()
	
	return HttpResponse(user.id)

@csrf_exempt
def allusers(request):
	users = User.objects.all()

	return HttpResponse(users)

@csrf_exempt
def storetext(request):
	data = request.raw_post_data
	data = json.loads(data)	
	battle = Battle(text=data)
	battle.save()
	
	print data['name']

	return HttpResponse(data['name'])

@csrf_exempt
def wait(request):
	while True:	
		print "Hi"
