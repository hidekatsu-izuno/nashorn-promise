/**
 * nashorn-promise
 *
 * @author hidekatsu.izuno@gmail.com (Hidekatsu Izuno)
 * @license MIT License
 */

(function(global) {
	'use strict';

	if (global.Promise === undefined) {
		var JCompletableFuture = Java.type('java.util.concurrent.CompletableFuture');
		var JCompleteFutureArray = Java.type('java.util.concurrent.CompletableFuture[]');
		var JPromiseException = Java.type('net.arnx.nashorn.lib.PromiseException');

		var Promise = function(resolver, futures) {
			var that = this;
			if (resolver instanceof JCompletableFuture) {
				that._future = resolver;
				that._futures = futures;
			} else {
				var func = Java.synchronized(function() {
					var status, result;
					(0, resolver)(function(value) {
						status = 'fulfilled';
						result = value;
					}, function(reason) {
						status = 'rejected';
						result = reason;
					});
					if (status == 'fulfilled') {
						return {
							result: result
						};
					} else if (status == 'rejected') {
						throw new JPromiseException(result);
					}
				}, global);
				if (Promise._pool) {
					that._future = JCompletableFuture.supplyAsync(func, Promise._pool);
				} else {
					that._future = JCompletableFuture.supplyAsync(func);
				}
			}
		};

		Promise.all = function(array) {
			var futures = array.map(function(elem) {
				if (elem instanceof Promise) {
					return elem._future;
				}
				return Promise.resolve(elem)._future;
			});
			return new Promise(JCompletableFuture.allOf(Java.to(futures, JCompleteFutureArray)), futures);
		};

		Promise.race = function(array) {
			var futures = array.map(function(elem) {
				if (elem instanceof Promise) {
					return elem._future;
				}
				return Promise.resolve(elem)._future;
			});
			return new Promise(JCompletableFuture.anyOf(Java.to(futures, JCompleteFutureArray)));
		};

		Promise.resolve = function(value) {
			if (value instanceof Promise) {
				return value;
			} else if (value != null
					&& (typeof value === 'function' || typeof value === 'object')
					&& typeof value.then === 'function') {
				return new Promise(function(fulfill, reject) {
					try {
						return {
							result: value.then(fulfill, reject)
						}
					} catch (e) {
						throw new JPromiseException(e);
					}
				});
			} else {
				return new Promise(JCompletableFuture.completedFuture({
					result: value
				}));
			}
		};

		Promise.reject = function(value) {
			return new Promise(function(fulfill, reject) {
				reject(value);
			});
		};

		Promise.prototype.then = function(onFulfillment, onRejection) {
			var that = this;
			return new Promise(that._future.handle(function(success, error) {
				if (success == null && error == null && that._futures != null) {
					success = {
						result: that._futures.map(function(elem) {
							return elem.get().result;
						})
					};
				}

				if (success != null) {
					if (typeof onFulfillment === 'function') {
						try {
							var value = success.result;
							if (value instanceof Promise) {
								return {
									result: (0, onFulfillment)(value._future.get().result)
								};
							}
							return {
								result: (0, onFulfillment)(success.result)
							};
						} catch (e) {
							throw new JPromiseException(e)
						}
					}
					return success;
				} else if (error != null) {
					var cerror = error;
					do {
						if (cerror instanceof JPromiseException) {
							error = cerror;
							break;
						}
					} while ((cerror = cerror.getCause()) != null);

					if (typeof onRejection === 'function') {
						try {
							var reason  = error;
							if (error instanceof JPromiseException) {
								reason = error.getResult();
							}

							return {
								result: (0, onRejection)(reason)
							};
						} catch (e) {
							throw new JPromiseException(e)
						}
					}
					throw error;
				}
			}));
		};

		Promise.prototype.catch = function(onRejection) {
			return this.then(null, onRejection);
		};

		global.Promise = Promise;
	}
})(Function('return this')());