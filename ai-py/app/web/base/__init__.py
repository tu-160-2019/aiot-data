from flask import Blueprint

#base = Blueprint('base', __name__, url_prefix='/base')
base = Blueprint('base', __name__)
from ..controller import *
