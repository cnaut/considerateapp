from django import forms
from django.db import models
from phonedata.models import User

class UserForm(forms.Form):
    name = forms.CharField()
class StatForm(forms.Form):
    user = forms.ChoiceField(widget=forms.Select, choices=[(user.id, user.name) for user in User.objects.all()])
    type = forms.CharField()
    value = forms.CharField()
