from django.db import models
from djangotoolbox.fields import ListField
from djangotoolbox.fields import EmbeddedModelField

class User(models.Model):
    name = models.TextField()
    photo = models.FileField(upload_to='user_photos')
    joined_on = models.DateTimeField(auto_now_add=True, null=True)

class Combatant(models.Model):
    user = EmbeddedModelField('User')
    health = models.IntegerField()

class War(models.Model):
    combatant = ListField(EmbeddedModelField('Combatant'))
    started_on = models.DateTimeField(auto_now_add=True, null=True)
    end_on = models.DateTimeField()

class Battle(models.Model):
    users = ListField()
    losers = ListField()
    lat = models.DecimalField(max_digits=8, decimal_places=4, null=True)
    long = models.DecimalField(max_digits=8, decimal_places=4, null=True)
    checkin_time = models.DateTimeField(auto_now_add=True, null=True)
    checkout_time = models.DateTimeField(auto_now_add=False, null=True)

class Training(models.Model):
    activity = models.TextField()
    checkin_time = models.DateTimeField(auto_now_add=True, null=True)
    checkout_time = models.DateTimeField()
