import json

from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponse, HttpResponseRedirect
from django.views.decorators.csrf import csrf_exempt
from django.db import models 
from battles.models import Battle
from battles.models import User 
from battles.forms import UserForm

def home(request):
	return HttpResponse("Moble Combat Home")

@csrf_exempt
def adduser(request):
	data = None;
	name = None;
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

@csrf_exempt
def allusers(request):
	users = User.objects.all()

	return HttpResponse(users)

@csrf_exempt
def userform(request):
	form = UserForm() 
	return render_to_response(
		'userform.html',
		{'form': form},
		context_instance=RequestContext(request)
	)

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
