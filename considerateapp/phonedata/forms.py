from django import forms
from django.db import models
from phonedata.models import User

class UserForm(forms.Form):
    phone_id = forms.CharField()

class UserStatChoiceField(forms.ModelChoiceField):
    def label_from_instance(self, obj):
        return obj['name']

class StatForm(forms.Form):
    user = UserStatChoiceField(queryset=User.objects.values(), empty_label="None")
    type = forms.CharField()
    value = forms.CharField()

class LbSearchForm(forms.Form):
    type = forms.CharField()
