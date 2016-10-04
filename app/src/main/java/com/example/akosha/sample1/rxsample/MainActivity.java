package com.example.akosha.sample1.rxsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    final CompositeSubscription lifeCycle = new CompositeSubscription();
    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text_view);
        button = (Button) findViewById(R.id.button);
//        concatObservable();
//        zipObservable().subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<String>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d(TAG, e.toString());
//                    }
//
//                    @Override
//                    public void onNext(String s) {
//                        Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
//                    }
//                });

//        getFirstObervable().flatMap(new Func1<String, Observable<String>>() {
//            @Override
//            public Observable<String> call(String s) {
//                return getThirdObservable(s);
//            }
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<String>() {
//                    @Override
//                    public void call(String s) {
//                        Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
//                    }
//                });

//        concatObservable();
//        mock1();
        stream();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
                onLifeCycleUnsubscribe(lifeCycle);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void stream() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("abc");
//                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        textView.setText(s);
                    }
                });


        lifeCycle.add(Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        subscriber.onNext("def");
                        subscriber.onCompleted();
                    }
                })
                        .delay(20, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(String s) {
                                textView.setText(s);
                            }
                        })
        );

    }

    private void onLifeCycleUnsubscribe(final CompositeSubscription lifeCycle) {

//        lifeCycle.unsubscribe();
        Observable.just(true).subscribeOn(Schedulers.computation()).observeOn(Schedulers.computation()).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean bool) {
                lifeCycle.unsubscribe();
            }
        });
    }

    private void mock1() {
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                subscriber.onNext("abc");
                subscriber.onCompleted();
            }
        });

        Observable<String> observable2 = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onError(new NullPointerException());
            }
        });

        Observable.concat(observable, observable2)
                .subscribeOn(Schedulers.io())
//                .onErrorReturn(new Func1<Throwable, String>() {
//                    @Override
//                    public String call(Throwable throwable) {
//                        return "on method error";
//                    }
//                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("complete: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("error: " + e);
                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println("next: " + s);
                    }
                });
    }


    private void concatObservable() {

        Observable.concat(getFirstObervable(), getSecondObservable())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.toString());
                    }

                    @Override
                    public void onNext(String s) {
//                        Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, s);
                    }
                });
    }

    private Observable<String> zipObservable() {
        return Observable.zip(getFirstObervable(), getSecondObservable(), new Func2<String, String, String>() {
            @Override
            public String call(String s, String s2) {
                if (s == null) {
                    Log.d(TAG, "firstnull");
                    Log.d(TAG, "second" + s2);
                } else if (s2 == null) {
                    Log.d(TAG, "secondnull");
                    Log.d(TAG, "first" + s);
                } else {
                    Log.d(TAG, "both_first" + s);
                    Log.d(TAG, "both_second" + s2);
                    return "both";
                }
                return null;
            }
        });
    }

    private Observable<String> getFirstObervable() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Log.d(TAG, "first");
                subscriber.onNext("a First Observable");
                subscriber.onCompleted();
            }
        }).delay(5, TimeUnit.SECONDS);
    }

    private Observable<String> getSecondObservable() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Log.d(TAG, "second");
                subscriber.onNext("a Second Observable");
                subscriber.onCompleted();
            }
        }).delay(1, TimeUnit.SECONDS);
    }

    private Observable<String> getThirdObservable(final String s) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                // save[[
                subscriber.onNext("msg_passed:" + s);
                subscriber.onCompleted();
            }
        });
    }

    private void mock() {
        final Observable<String> fallbackObservable =
                Observable
                        .create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(Subscriber<? super String> subscriber) {
                                Log.d(TAG, "emitting A Fallback");
                                subscriber.onNext("A Fallback");
                                subscriber.onCompleted();
                            }
                        })
                        .delay(1, TimeUnit.SECONDS)
                        .onErrorReturn(new Func1<Throwable, String>() {
                            @Override
                            public String call(Throwable throwable) {
                                Log.d(TAG, "emitting Fallback Error");
                                return "Fallback Error";
                            }
                        })
                        .subscribeOn(Schedulers.immediate());

        Observable<String> stringObservable =
                Observable
                        .create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(Subscriber<? super String> subscriber) {
                                Log.d(TAG, "emitting B");
                                subscriber.onNext("B");
                                subscriber.onCompleted();
                            }
                        })
                        .delay(1, TimeUnit.SECONDS)
                        .flatMap(new Func1<String, Observable<String>>() {
                            @Override
                            public Observable<String> call(String s) {
                                Log.d(TAG, "flatMapping B");
                                return Observable
                                        .create(new Observable.OnSubscribe<String>() {
                                            @Override
                                            public void call(Subscriber<? super String> subscriber) {
                                                Log.d(TAG, "emitting A");
                                                subscriber.onNext("A");
                                                subscriber.onCompleted();
                                            }
                                        })
                                        .delay(1, TimeUnit.SECONDS)
                                        .map(new Func1<String, String>() {
                                            @Override
                                            public String call(String s) {
                                                Log.d(TAG, "A completes but contains invalid data - throwing error");
                                                return String.valueOf(Observable.error(new Throwable("YUCK!")));
//                                                throw new NotImplementedException("YUCK!");
                                            }
                                        })
                                        .onErrorResumeNext(fallbackObservable)
                                        .subscribeOn(Schedulers.newThread());
                            }
                        })
                        .onErrorReturn(new Func1<Throwable, String>() {
                            @Override
                            public String call(Throwable throwable) {
                                Log.d(TAG, "emitting Return Error");
                                return "Return Error";
                            }
                        })
                        .subscribeOn(Schedulers.newThread());

        Subscription subscription = stringObservable.subscribe(
                new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d(TAG, "onNext " + s);
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.d(TAG, "onError");
                    }
                },
                new Action0() {
                    @Override
                    public void call() {
                        Log.d(TAG, "onCompleted");
                    }
                });
    }
}
