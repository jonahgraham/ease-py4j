import traceback

from py4j.clientserver import ClientServer, JavaParameters, PythonParameters


class ScriptEngineExecute(object):
    def __init__(self):
        pass

    def set_gateway(self, gateway):
        self.gateway = gateway
        self.locals = dict()
        self.locals['__ease_gateway'] = gateway

    def execute(self, code_text):
        try:
            exec code_text in self.locals
        except:
            traceback.print_exc()

    def internalGetVariable(self, name):
        return self.locals.get(name)

    def internalGetVariables(self):
        return self.locals

    def internalHasVariable(self, name):
        return self.locals.has_key(name)

    def internalSetVariable(self, name, content):
        self.locals[name] = content


    class Java:
        implements = ['org.eclipse.ease.lang.python.py4j.internal.IPythonSideEngine']


def main():
    engine = ScriptEngineExecute()
    gateway = ClientServer(java_parameters=JavaParameters(auto_convert=True),
                          python_parameters=PythonParameters(),
                          python_server_entry_point=engine)
    engine.set_gateway(gateway)

if __name__ == '__main__':
    main()
