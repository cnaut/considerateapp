import json
import django.dispatch 

from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponse, HttpResponseRedirect
from django.views.decorators.csrf import csrf_exempt
from django.db import models 
from django.core import serializers
from battles.models import Battle
from battles.models import User 
from battles.forms import UserForm
from django.dispatch import receiver

def home(request):
	return HttpResponse("Moble Combat Home")

#Takes a name and a photo and adds user to the database then returns user id 
@csrf_exempt
def adduser(request):
	data = None
	name = None
	if(request.POST.get('name')):
		data = request.POST
		name = data.get('name')
	else:		
		data = request.raw_post_data
		data = json.loads(data)
		name = data['name']	
		

	user = User(name=name, photo=request.FILES['photo'])
	user.save()
	
	return HttpResponse(user.id)

#Takes list of users and creates battle then returns battle id
def startbattle(request):
	data = request.raw_post_data
	data = json.loads(data)
	
 	users = []
	for user in data['users']:
		users.append(user.encode("ascii"))
	battle = Battle(users=users, losers=[])
	print battle
	battle.save()
	return HttpResponse(battle.id)

#Takes user id and returns latest battle that user was in - still not done
@csrf_exempt
def getbattle(request):
	id = None
	if(request.POST.get('id')):
		id = request.POST.get('id')
	else:		
		id = request.GET.get('id')	
		
	battle = Battle.objects.order_by("checkin_time").get()[0:1]	
	return HttpResponse(battle.id)

#Takes a battle id and a user id and adds user id to list of losers for battle 
@csrf_exempt
def declaredefeat(request):
	data = request.GET
	battle = Battle.objects.get(id=data.get('battleid'))	
	battle.losers.extend([data.get('userid')]) 
	battle.save()
	return HttpResponse(battle.losers)
