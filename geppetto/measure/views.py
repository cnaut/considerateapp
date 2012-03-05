# Create your views here.
from django.shortcuts import render_to_response
from measure.models import TextMessage

def home(request):
    message = TextMessage(text="hi", sender="Charles", receiver="Pinokia" )
    return render_to_response('measure/home.html', {'message': message})
