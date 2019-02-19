var objects = {};

function registerObject(obj) {
    if (objects[obj] === undefined) {
        objects[obj] = {};
    }
}

function registerMethod(obj, name, fn) {
    if (objects[obj] === undefined) registerObject(obj);
    objects[obj][name] = eval(fn);
}

function getFunction(obj, name) {
    return objects[obj][name];
}

function unregisterObject(obj) {
    delete objects[obj];
}

function unregisterMethod(obj, name) {
    delete objects[obj][name];
}

function invoke(obj, name) {
    var args = Array.prototype.slice.call(arguments);
    args.splice(0, 2);

    var fn = getFunction(obj, name);
    if (fn === undefined) return null;
    return fn.apply(null, args)
}
